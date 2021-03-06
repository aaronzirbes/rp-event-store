package org.zirbes.eventsource.modules

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Scopes
import com.google.inject.Singleton

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.handlers.AggregateReaderHandler
import org.zirbes.eventsource.handlers.EventReaderHandler
import org.zirbes.eventsource.handlers.EventWriterHandler
import org.zirbes.eventsource.handlers.HealthHandler
import org.zirbes.eventsource.services.AggregatePublisher
import org.zirbes.eventsource.services.CassandraClusterService
import org.zirbes.eventsource.services.ElasticsearchService
import org.zirbes.eventsource.services.EventLogReader

@CompileStatic
@Slf4j
class EventSourceModule extends AbstractModule {

    static final String DEFAULT_HOST = 'docker'
    static final Integer DEFAULT_PORT = 9042
    static final String DEFAULT_KEYSPACE = 'bicycle'

    private final String cassandraHost
    private final String cassandraPort
    private final String keyspace

    private Cluster cluster

    EventSourceModule(Map<String, Object> config) {
        this.cassandraHost = config.host ?: DEFAULT_HOST
        this.cassandraPort = config.port ?: DEFAULT_PORT
        this.keyspace = config.keyspace ?: DEFAULT_KEYSPACE
    }

    @Override
    protected void configure() {
        bind(HealthHandler).in(Scopes.SINGLETON)
        bind(EventWriterHandler).in(Scopes.SINGLETON)
        bind(EventReaderHandler).in(Scopes.SINGLETON)
        bind(AggregateReaderHandler).in(Scopes.SINGLETON)
        bind(CassandraClusterService).in(Scopes.SINGLETON)
        bind(ElasticsearchService).in(Scopes.SINGLETON)
        bind(EventLogReader).in(Scopes.SINGLETON)
        bind(AggregatePublisher).in(Scopes.SINGLETON)
    }

    @Provides
    @Singleton
    Cluster provideCassandraCluster() {
        cluster = Cluster.builder().addContactPoint(cassandraHost).build()
        return cluster
    }

    @Provides
    @Singleton
    Session provideCassandraSession() {
        if (!cluster) { cluster = provideCassandraCluster() }
        return cluster.connect(keyspace)
    }

}
