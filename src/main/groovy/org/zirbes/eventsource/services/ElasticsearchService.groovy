package org.zirbes.eventsource.services

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import ratpack.server.Service
import ratpack.server.StartEvent
import ratpack.server.StopEvent

import static org.elasticsearch.node.NodeBuilder.*

@CompileStatic
@Slf4j
class ElasticsearchService implements Service {

    protected Node node
    protected Client client

    Client getElasticsearchClient() {
        return client
    }

    @Override
    void onStart(StartEvent event) {
        log.info 'Elasticsearch service starting'
        setTransportClient()
        ElasticsearchClientTester.test(client)
    }

    @Override
    void onStop(StopEvent event) {
        log.info 'Elasticsearch service stopping'
        if (node) { node.close() }
    }

    protected void setTransportClient() {
        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("docker", 9300))
    }

    protected void setNodeClient() {
        node = nodeBuilder().node()
        client = node.client()
    }

}
