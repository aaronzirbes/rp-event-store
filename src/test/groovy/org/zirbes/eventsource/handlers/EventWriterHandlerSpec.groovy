package org.zirbes.eventsource.handlers

import org.zirbes.eventsource.BikeEventFixtureBuilder
import org.zirbes.eventsource.JsonSpecification
import org.zirbes.eventsource.events.VehicleEvent
import org.zirbes.eventsource.services.EventLogWriter

import static ratpack.rx.RxRatpack.promise

import ratpack.exec.Promise
import ratpack.http.Response
import ratpack.parse.Parse
import ratpack.groovy.handling.GroovyContext

import rx.Observable

class EventWriterHandlerSpec extends JsonSpecification {

    HealthHandler health
    EventLogWriter writer
    EventWriterHandler handler

    static final String AGGREGATE_ID = 'dd4cad36-85be-48b3-b2e1-51cc93712e4c'

    void setup() {
        health = Mock()
        writer = Mock()
        handler = new EventWriterHandler(writer, health)
    }

    void 'have context will render'() {
        given:
        GroovyContext context = Mock()
        Promise promise = Promise.of({
            return new BikeEventFixtureBuilder().buildEvents()[0]
        })

        when:
        handler.handle(context)

        then:
        1 * context.parse(_ as Parse) >> promise
        1 * context.render(_ as Promise)
        0 * _

    }

    void 'can you handle this ... event'() {
        given:
        VehicleEvent event = new BikeEventFixtureBuilder().buildEvents()[0]
        GroovyContext context = Mock()
        Response response = Mock()

        when:
        handler.handleEvent(event, context)

        then:
        1 * writer.writeEventAsync(event)
        1 * context.getResponse() >> response
        1 * response.status(201)
        0 * _

    }

}
