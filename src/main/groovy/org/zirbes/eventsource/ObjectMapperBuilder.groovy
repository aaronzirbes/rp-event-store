package org.zirbes.eventsource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule

class ObjectMapperBuilder {

    static ObjectMapper build() {
        return configure(new ObjectMapper())
    }

    static ObjectMapper configure(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false)
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true)
        objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
    }

}


