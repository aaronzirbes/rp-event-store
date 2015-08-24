package org.zirbes.eventsource.services

import com.datastax.driver.core.Cluster
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
    protected Session session

    static final String createKeyspace = '''
            CREATE KEYSPACE bicycle
            WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};
            '''

    static final Map<String, String> createTables = [
        'events': '''CREATE TABLE bicycle.events (
                  event_id text,
                  time timestamp,
                  event_type text,
                  user_id text,
                  source_system text,
                  data blob,
                  PRIMARY KEY((event_id), time, event_id)
              ) WITH CLUSTERING ORDER BY (time DESC);
            '''
    ]

    @Inject
    CassandraService(Cluster cluster, Session session) {
        this.cluster = cluster
        this.session = session
    }

    @Override
    void onStart(StartEvent event) {
        // create tables
    }

    @Override
    void onStop(StopEvent event) {
        cluster.close()
    }

}
