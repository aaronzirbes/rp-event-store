package org.zirbes.eventsource.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.thirdchannel.eventsource.Aggregate

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject

import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client
import org.zirbes.eventsource.ObjectMapperBuilder

/** Write aggregate to elastic search */
@CompileStatic
@Slf4j
class AggregateWriter {

    static final String INDEX = 'eventsource'

    protected final ObjectMapper objectMapper

    // Write to elastic search
    protected final ElasticsearchService elasticsearchService

    @Inject
    AggregateWriter(ElasticsearchService elasticsearchService) {
        this.objectMapper = ObjectMapperBuilder.build()
        this.elasticsearchService = elasticsearchService
    }

    String json(Aggregate aggregate) {
        return objectMapper.writeValueAsString(aggregate)
    }

    void writeAggregate(Aggregate aggregate) {
        Client client = elasticsearchService.getElasticsearchClient()
        String id = aggregate.id.toString()
        String type = aggregate.class.simpleName
        IndexResponse response = client.prepareIndex(INDEX, type, id)
                                       .setSource(json(aggregate))
                                       .execute()
                                       .actionGet()

        if (response.created) {
            log.info "Wrote document: index=${response.index} " +
                                     "type=${response.type} " +
                                     "id=${response.id} " +
                                     "version=${response.version}"
        } else {
            log.error "Document was not created."
        }

    }

}
