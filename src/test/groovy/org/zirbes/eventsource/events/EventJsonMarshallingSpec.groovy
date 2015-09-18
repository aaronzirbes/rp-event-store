package org.zirbes.eventsource.domain

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class EventJsonMarshallingSpec extends Specification {

    @Shared
    private ObjectMapper objectMapper

    void setupSpec() {
        objectMapper = new ObjectMapper()
    }

    @Unroll
    void 'Marshalling #clazz JSON and back to JSON preserves all data using #fixture'() {

        given: 'a JSON payload'
        String json = jsonFromFixture(fixture)

        when: 'we marshal it to the object'
        def obj = objectMapper.readValue(json, clazz)

        and: "we copy it to a Map and remove fields we don't care about"
        Map map = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>(){})
        map.remove('id')
        map.remove('date')
        map.remove('dateEffective')
        if (!map.data) { map.remove('data') }

        and: 'we marshaled it back to JSON'
        String marshaled = stripWhiteSpace(objectMapper.writeValueAsString(map))

        then: 'it should match'
        marshaled == json

        where:
        fixture                 | clazz
        'adjustSeatHeightEvent' | AdjustSeatHeightEvent
        'flatTireEvent'         | FlatTireEvent
        'inflateTireEvent'      | InflateTireEvent
        'lockBikeEvent'         | LockBikeEvent
        'purchaseBikeEvent'     | PurchaseBikeEvent
        'repairFlatEvent'       | RepairFlatEvent
        'rideBikeEvent'         | RideBikeEvent
        'unockBikeEvent'        | UnockBikeEvent
        'vehicleEvent'          | VehicleEvent

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
