package org.zirbes.eventsource.services

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.google.inject.Inject

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import ratpack.server.Service
import ratpack.server.StartEvent
import ratpack.server.StopEvent

/**
 * .registryOf(r -> r.add(service))
 *
 * https://github.com/ratpack/ratpack/blob/master/ratpack-core/src/main/java/ratpack/server/Service.java
 *
 * http://ratpack.io/manual/current/api/ratpack/server/Service.html
 */
@CompileStatic
@Slf4j
class CassandraClusterService implements Service {

    protected Cluster cluster

    // TODO: Pull from config
    static final String KEYSPACE = 'bicycle'
    static final String REPLICATION_FACTOR = 3

    static final Map<String, String> CREATE_TABLES = [
        'event': '''CREATE TABLE bicycle.event (
                         id uuid,
                         type text,
                         revision int,
                         aggregate_id uuid,
                         time timestamp,
                         user_id text,
                         date_effective timestamp,
                         source_system text,
                         data blob,
                         PRIMARY KEY((aggregate_id), time, id)
                     ) WITH CLUSTERING ORDER BY (time ASC, id DESC);''',

        'snapshot': '''CREATE TABLE bicycle.snapshot (
                         id uuid,
                         type text,
                         revision int,
                         aggregate_id uuid,
                         time timestamp,
                         data blob,
                         PRIMARY KEY((aggregate_id), time, id)
                     ) WITH CLUSTERING ORDER BY (time DESC, id ASC);'''
    ].asImmutable()

    @Inject
    CassandraService(Cluster cluster) {
        this.cluster = cluster
    }

    @Override
    void onStart(StartEvent event) {
        log.info 'Cassandra service starting'

        // create keyspace
        Session system = cluster.connect('system')
        createKeyspace system, KEYSPACE
        system.close()

        // create tables
        Session session = cluster.connect('system')
        CREATE_TABLES.each{ String table, String cql ->
            createTable session, table, cql
        }
        session.close()

    }

    protected void createKeyspace(Session session, String keyspace) {
        ResultSet rs = session.execute('SELECT keyspace_name FROM system.schema_keyspaces')
        Set<String> keyspaces = rs.iterator().collect{ Row row -> row.getString('keyspace_name') }.toSet()

        if (keyspace in keyspaces) {
            log.info "Found keyspace ${keyspace}"
        } else {
            log.info("Creating new keyspace ${keyspace}")
            final String cql = "CREATE KEYSPACE ${keyspace} " +
                    "WITH replication = {'class':'SimpleStrategy', 'replication_factor':${REPLICATION_FACTOR}};"
            session.execute(cql)
        }
    }

    protected void createTable(Session session, String table, String cql) {
        final String tableCql = 'SELECT columnfamily_name ' +
                'FROM system.schema_columnfamilies ' +
                "WHERE keyspace_name = '${KEYSPACE}';"

        ResultSet rs = session.execute(tableCql)
        Set<String> tables = rs.iterator().collect{ Row row -> row.getString('columnfamily_name') }.toSet()

        if (table in tables) {
            log.info "Found table ${table}"
        } else {
            log.info("Creating new table ${table}")
            session.execute(cql)
        }
    }

    @Override
    void onStop(StopEvent event) {
        log.info 'Cassandra service stopping'
        cluster.close()
    }

}
