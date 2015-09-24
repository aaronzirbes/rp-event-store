package org.zirbes.eventsource.services

import org.zirbes.eventsource.BikeEventFixtureBuilder
import org.zirbes.eventsource.aggregates.Bicycle
import org.zirbes.eventsource.events.VehicleEvent

import rx.Observable

import spock.lang.Specification

class AggregateReaderSpec extends Specification {

    EventLogReader eventLogReader
    AggregateReader reader

    void setup() {
        eventLogReader = Mock()
        reader = new AggregateReader(eventLogReader)
    }

    void 'can build aggregate from events'()  {
        given:
        UUID aggregateId = UUID.fromString("dd4cad36-85be-48b3-b2e1-51cc93712e4c")
        List<VehicleEvent> bikeEvents = new BikeEventFixtureBuilder().buildEvents()
        Observable<VehicleEvent> observable = Observable.from(bikeEvents)

        when:
        Bicycle bicycle = reader.getAggregate(aggregateId).toBlocking().single()

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
        1 * eventLogReader.getEvents(aggregateId) >> observable
        0 * _
    }

}

