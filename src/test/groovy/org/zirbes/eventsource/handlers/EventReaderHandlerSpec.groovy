package org.zirbes.eventsource.handlers

import org.zirbes.eventsource.BikeEventFixtureBuilder
import org.zirbes.eventsource.events.VehicleEvent
import org.zirbes.eventsource.services.EventLogReader

import ratpack.exec.Promise
import ratpack.groovy.handling.GroovyContext
import ratpack.path.PathTokens

import rx.Observable

import spock.lang.Specification

class EventReaderHandlerSpec extends Specification {

    HealthHandler health
    EventLogReader reader
    EventReaderHandler handler

    static final String AGGREGATE_ID = 'dd4cad36-85be-48b3-b2e1-51cc93712e4c'

    void setup() {
        health = Mock()
        reader = Mock()
        handler = new EventReaderHandler(reader, health)
    }

    void 'have context will render'() {
        given:
        UUID aggregateUuid = UUID.fromString(AGGREGATE_ID)
        GroovyContext context = Mock()
        PathTokens tokens = Mock()
        List<VehicleEvent> events = new BikeEventFixtureBuilder().buildEvents()
        Observable observable = Observable.from(events)

        when:
        handler.handle(context)

        then:
        1 * context.getPathTokens() >> tokens
        1 * tokens.get('aggregateId') >>  AGGREGATE_ID
        1 * context.render(_ as Promise)
        1 * reader.getEvents(aggregateUuid) >> observable
        0 * _

    }

}
