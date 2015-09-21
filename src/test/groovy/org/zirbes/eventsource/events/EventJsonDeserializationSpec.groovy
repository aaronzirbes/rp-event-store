package org.zirbes.eventsource.events

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import groovy.mock.interceptor.Ignore

import org.junit.Ignore
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

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class EventJsonDeserializationSpec extends JsonSpecification {

    void 'can marshal list of events to JSON and back'() {
        given: 'a list of events'
        List<VehicleEvent> events = [
            new PurchaseBikeEvent(make: 'Surly', model: 'Steamroller', wheelSize: 700),
            new AdjustSeatHeightEvent(height: 4),
            new InflateTireEvent(tire: 'front', psi: 80),
            new InflateTireEvent(tire: 'rear', psi: 80),
            new FlatTireEvent(tire: 'front'),
            new InflateTireEvent(tire: 'front', psi: 20),
            new RepairFlatEvent(tire: 'front'),
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
        events.size() == 11
        events[0].class == PurchaseBikeEvent
        events[1].class == AdjustSeatHeightEvent
        events[2].class == InflateTireEvent
        events[3].class == InflateTireEvent
        events[4].class == FlatTireEvent
        events[5].class == InflateTireEvent
        events[6].class == RepairFlatEvent
        events[7].class == InflateTireEvent
        events[8].class == LockBikeEvent
        events[9].class == UnockBikeEvent
        events[10].class == RideBikeEvent

    }

}
