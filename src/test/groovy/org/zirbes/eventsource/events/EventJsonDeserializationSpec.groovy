package org.zirbes.eventsource.events

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import groovy.mock.interceptor.Ignore

import org.junit.Ignore
import org.zirbes.eventsource.JsonSpecification

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class EventJsonDeserializationSpec extends JsonSpecification {

    void 'can marshal list of events to JSON and back'() {
        given: 'a list of events'
        List<VehicleEvent> events = [
            new PurchaseBikeEvent(make: 'Surly', model: 'Steamroller', wheelSize: 700),
            new AdjustSeatHeightEvent(height: 4),
            new TurnOnLightEvent(light: 'rear'),
            new InflateTireEvent(tire: 'front', psi: 80),
            new InflateTireEvent(tire: 'rear', psi: 80),
            new FlatTireEvent(tire: 'front'),
            new InflateTireEvent(tire: 'front', psi: 20),
            new RepairFlatEvent(tire: 'front'),
            new TurnOffLightEvent(light: 'rear'),
            new InflateTireEvent(tire: 'front', psi: 70),
            new LockBikeEvent(),
            new UnockBikeEvent(),
            new RideBikeEvent(lat: 45.003966, lon: -93.247044, headingDegrees: 0, velocity: 12)
        ]

        when: 'they become JSON'
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(events)
        println "events: ${json}"

        and: 'we marshal them back again'
        TypeReference typeRef = new TypeReference<List<VehicleEvent>>() {}
        List<VehicleEvent> backAgain = objectMapper.readValue(json, typeRef)

        then:
        events.size() == 13
        events[0].class == PurchaseBikeEvent
        events[1].class == AdjustSeatHeightEvent
        events[2].class == TurnOnLightEvent
        events[3].class == InflateTireEvent
        events[4].class == InflateTireEvent
        events[5].class == FlatTireEvent
        events[6].class == InflateTireEvent
        events[7].class == RepairFlatEvent
        events[8].class == TurnOffLightEvent
        events[9].class == InflateTireEvent
        events[10].class == LockBikeEvent
        events[11].class == UnockBikeEvent
        events[12].class == RideBikeEvent

    }

}
