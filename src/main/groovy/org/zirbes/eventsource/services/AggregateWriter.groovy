package org.zirbes.eventsource.services

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.util.logging.Slf4j
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client

import javax.inject.Inject

import org.zirbes.eventsource.ObjectMapperBuilder
import org.zirbes.eventsource.aggregates.Bicycle

@Slf4j
/** Write aggregate to elastic search */
class AggregateWriter {

    static final String INDEX = 'eventsource'
    static final String MAPPING = 'bicycle'

    protected final ObjectMapper objectMapper

    // Write to elastic search
    protected final ElasticsearchService elasticsearchService

    @Inject
    AggregateWriter(ElasticsearchService elasticsearchService) {
        this.objectMapper = ObjectMapperBuilder.build()
        this.elasticsearchService = elasticsearchService
    }

    String json(Bicycle bicycle) {
        return objectMapper.writeValueAsString(bicycle)
    }

    void writeAggregate(Bicycle bicycle) {
        Client client = elasticsearchService.getElasticsearchClient()
        IndexResponse response = client.prepareIndex(INDEX, MAPPING)
                                       .setSource(json(bicycle))
                                       .execute()
                                       .actionGet()

        log.info "Wrote: index=${response.index} " +
                 "type=${response.type} " +
                 "id=${response.id} " +
                 "version=${response.version} " +
                 "created=${response.created}"

    }

}
