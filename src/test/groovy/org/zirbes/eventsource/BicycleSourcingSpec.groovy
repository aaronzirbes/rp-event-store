package org.zirbes.eventsource

import org.zirbes.eventsource.domain.Bicycle
import org.zirbes.eventsource.events.VehicleEvent

import spock.lang.Specification
import rx.Observable

class BicycleSourcingSpec extends Specification {

    void 'can build aggregate from event source'() {
        given:
        Bicycle bicycle = new Bicycle(
            id: UUID.fromString("dd4cad36-85be-48b3-b2e1-51cc93712e4c"),
        )
        List<VehicleEvent> bikeEvents = new BikeEventFixtureBuilder().buildEvents()

        when:
        bicycle.applyChanges(bikeEvents)

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

