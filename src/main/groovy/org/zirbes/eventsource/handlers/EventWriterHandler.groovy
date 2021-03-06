package org.zirbes.eventsource.handlers

import com.google.inject.Inject
import com.thirdchannel.eventsource.AbstractEvent

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.events.VehicleEvent
import org.zirbes.eventsource.services.EventLogWriter

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.jackson.JsonRender

import static ratpack.jackson.Jackson.fromJson
import static ratpack.jackson.Jackson.json


@CompileStatic
@Slf4j
class EventWriterHandler extends GroovyHandler {

    protected final HealthHandler health
    protected final EventLogWriter writer


    @Inject
    EventWriterHandler(EventLogWriter writer, HealthHandler health) {
        this.health = health
        this.writer = writer
    }


    @Override
    protected void handle(GroovyContext context) {
        context.render(
            context.parse(fromJson(VehicleEvent)).map{ AbstractEvent event ->
                return handleEvent(event, context)
            }
        )
    }

    protected JsonRender handleEvent(AbstractEvent event, GroovyContext context) {
        UUID key = UUID.randomUUID()
        LocalDateTime now = LocalDateTime.now()
        event.id = key
        event.date = now.toDate()
        if (event) {
            // Overwrite event ID / date
            writer.writeEvent(event)
            log.info 'wrote event event.id={}', event.id
            // return response
            context.response.status(201)
            return json([key: key, date: now, message: 'queued'])
        } else {
            context.response.status(204)
            return json([key: null, date: now, message: 'empty request'])
        }
    }

}
