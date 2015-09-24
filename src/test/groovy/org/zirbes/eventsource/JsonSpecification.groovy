package org.zirbes.eventsource

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Shared
import spock.lang.Specification

class JsonSpecification extends Specification {

    @Shared
    protected ObjectMapper objectMapper

    void setupSpec() {
        objectMapper = new ObjectMapper()
        objectMapper.setSerializationInclusion(Include.NON_NULL)
    }

    protected String toJson(Object object) {
        return objectMapper.writeValueAsString(object)
    }

    protected String jsonFromFixture(String fixture) {
        String path = "/fixtures/${fixture}.json"
        return jsonFromResource(path)
    }

    protected String jsonFromResource(String resourcePath) {
        InputStream inputStream = this.class.getResourceAsStream(resourcePath)
        if (inputStream) {
            return stripWhiteSpace(inputStream.text)
        }
        throw new FileNotFoundException(resourcePath)
    }

    protected String stripWhiteSpace(String str) {
        StringBuffer out = new StringBuffer()
        str.eachLine{
            out << it.replaceFirst(/": /, '":').replaceFirst(/" : /, '":').trim()
        }
        return out.toString()
    }

}
