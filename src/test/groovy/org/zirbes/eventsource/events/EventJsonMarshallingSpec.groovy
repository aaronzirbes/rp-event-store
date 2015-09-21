package org.zirbes.eventsource.events

import com.fasterxml.jackson.core.type.TypeReference

import org.zirbes.eventsource.JsonSpecification
import org.zirbes.eventsource.events.AdjustSeatHeightEvent
import org.zirbes.eventsource.events.FlatTireEvent
import org.zirbes.eventsource.events.InflateTireEvent
import org.zirbes.eventsource.events.LockBikeEvent
import org.zirbes.eventsource.events.PurchaseBikeEvent
import org.zirbes.eventsource.events.RepairFlatEvent
import org.zirbes.eventsource.events.RideBikeEvent
import org.zirbes.eventsource.events.UnockBikeEvent
import org.zirbes.eventsource.events.VehicleEvent

import spock.lang.Unroll

/** Test for marshalling incoming JSON to the corresponding VehicleEvent */
class EventJsonMarshallingSpec extends JsonSpecification {

    @Unroll
    void 'Marshalling #clazz JSON and back to JSON preserves all data using #fixture'() {

        given: 'a JSON payload'
        String json = jsonFromFixture(fixture)

        when: 'we marshal it to the object'
        VehicleEvent obj = objectMapper.readValue(json, VehicleEvent)

        then:
        obj.class == clazz

        when: "we copy it to a Map and remove fields we don't care about"
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

}
