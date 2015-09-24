
package org.zirbes.eventsource.services

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client
import org.zirbes.eventsource.ObjectMapperBuilder
import org.zirbes.eventsource.aggregates.Bicycle

import static org.elasticsearch.node.NodeBuilder.*

@CompileStatic
@Slf4j
class ElasticsearchClientTester {

    static final String INDEX = 'testindex'
    static final String MAPPING = 'testmapping'

    static void test(Client client) {
        UUID uuid = UUID.randomUUID()
        String id = uuid.toString()
        Bicycle bicycle = getSampleBicycle(uuid)
        ObjectMapper mapper = ObjectMapperBuilder.build()
        String json = mapper.writeValueAsString(bicycle)

        IndexResponse index = client.prepareIndex(INDEX, MAPPING, id)
                                       .setSource(json)
                                       .execute()
                                       .actionGet()

        log.info "Wrote test object: index=${index.index} " +
                 "type=${index.type} " +
                 "id=${index.id} " +
                 "version=${index.version} " +
                 "created=${index.created}"

        int tries = 0
        while (tries < 25) {
            tries++
            GetResponse get = client.prepareGet(INDEX, MAPPING, id)
                    .execute()
                    .actionGet()
            if (!get.exists) {
                log.warn "Object does not exist yet, waiting..."
                Thread.sleep(200)
            }
        }

        DeleteResponse delete = client.prepareDelete(INDEX, MAPPING, id)
                .execute()
                .actionGet()

        log.info "Deleted test object: index=${delete.index}" +
                "type=${delete.type} " +
                "id=${delete.id} " +
                "version=${delete.version} " +
                "found=${delete.found} "
    }

    protected static Bicycle getSampleBicycle(UUID uuid) {
        return new Bicycle(
            id: uuid,
            make: 'Surly',
            model: 'Pugsley',
            wheelSize: 29,
            seatHeight: 10
        )
    }

}
