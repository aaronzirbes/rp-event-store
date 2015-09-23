package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.thirdchannel.eventsource.AbstractEvent

import groovy.util.logging.Slf4j

@Slf4j
abstract class AbstractWriter {

    protected BoundStatement insert

    protected final Session session
    protected final ObjectMapper objectMapper
    protected final TypeReference mapRef = new TypeReference<Map<String, Object>>() {}

    abstract protected String getInsertStatement()
    abstract protected List<String> getBaseFields()

    AbstractWriter(Session session) {
        this.session = session
        this.objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
    }

    void setup() {
        PreparedStatement ps = session.prepare(insertStatement)
        this.insert = new BoundStatement(ps)
    }

    /** Uses Jackson for now, it'd be faster to use apache Avro down the line */
    protected byte[] dataFromAbstract(Object event) {
        // TODO: Use ratpack.jackson.Jackson
        // Convert to map
        Map<String, Object> eventMap = objectMapper.convertValue(event, mapRef)
        // remove base fields
        eventMap.keySet().removeAll(baseFields)
        // encode to JSON
        String json = objectMapper.writeValueAsString(eventMap)
        if (json == '{}') { return ''.bytes }
        return json.bytes
    }

}