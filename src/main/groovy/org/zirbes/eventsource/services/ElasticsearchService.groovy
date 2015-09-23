package org.zirbes.eventsource.services

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import org.zirbes.eventsource.ObjectMapperBuilder
import org.zirbes.eventsource.aggregates.Bicycle
import ratpack.server.Service
import ratpack.server.StartEvent
import ratpack.server.StopEvent

import static org.elasticsearch.node.NodeBuilder.*

/**
 * .registryOf(r -> r.add(service))
 *
 * https://github.com/ratpack/ratpack/blob/master/ratpack-core/src/main/java/ratpack/server/Service.java
 *
 * http://ratpack.io/manual/current/api/ratpack/server/Service.html
 */
@CompileStatic
@Slf4j
class ElasticsearchService implements Service {

    static final String INDEX = 'eventsource'
    static final String MAPPING = 'bicycle'
    protected Node node
    protected Client client

    @Override
    void onStart(StartEvent event) {
        log.info 'Elasticsearch service starting'
        // https://github.com/seapy/dockerfiles/blob/master/elasticsearch/elasticsearch.yml
        node = nodeBuilder().node()
        client = node.client()
        testClient()
    }

    void testClient() {
        String id = 'dd4cad36-85be-48b3-b2e1-51cc93712e4c'
        UUID uuid = UUID.fromString(id)
        Bicycle bicycle = getSampleBicycle(uuid)
        ObjectMapper mapper = ObjectMapperBuilder.build()
        String json = mapper.writeValueAsString(bicycle)

        IndexResponse response = client.prepareIndex(INDEX, MAPPING)
                                       .setSource(json)
                                       .execute()
                                       .actionGet()

        log.info "Wrote: index=${response.index} " +
                 "type=${response.type} " +
                 "id=${response.id} " +
                 "version=${response.version} " +
                 "created=${response.created}"

    }

    protected Bicycle getSampleBicycle(UUID uuid) {
        return new Bicycle(
            id: uuid,
            make: 'Surly',
            model: 'Pugsley',
            wheelSize: 29,
            seatHeight: 10
        )
    }

    Client getElasticsearchClient() {
        return client
    }

    @Override
    void onStop(StopEvent event) {
        log.info 'Elasticsearch service stopping'
        node.close()
    }

}
