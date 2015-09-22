package org.zirbes.eventsource

import org.zirbes.eventsource.domain.Bicycle
import org.zirbes.eventsource.events.AdjustSeatHeightEvent
import org.zirbes.eventsource.events.FlatTireEvent
import org.zirbes.eventsource.events.InflateTireEvent
import org.zirbes.eventsource.events.LockBikeEvent
import org.zirbes.eventsource.events.PurchaseBikeEvent
import org.zirbes.eventsource.events.RepairFlatEvent
import org.zirbes.eventsource.events.RideBikeEvent
import org.zirbes.eventsource.events.TurnOffLightEvent
import org.zirbes.eventsource.events.TurnOnLightEvent
import org.zirbes.eventsource.events.UnockBikeEvent

import spock.lang.Specification

class BicycleSourcingSpec extends Specification {

    void 'can build aggregate from event source'() {
        given:
        Bicycle bicycle = new Bicycle(
            id: UUID.fromString("dd4cad36-85be-48b3-b2e1-51cc93712e4c"),
        )

        when:
        bicycle.applyChanges([
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
        ])

        then:
        bicycle.make == 'Surly'
        bicycle.model == 'Steamroller'
        bicycle.wheelSize == 700
        bicycle.location.lat == 45.003966
        bicycle.location.lon == -93.247044
        bicycle.headingDegrees == 0
        bicycle.velocity == 12
        !bicycle.locked
        !bicycle.lights.front.on
        bicycle.lights.front.color == 'white'
        bicycle.lights.rear.color == 'red'
        bicycle.tires.front.psi == 70
    }
}

