package org.zirbes.eventsource.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject

import com.fasterxml.jackson.datatype.joda.JodaModule

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.domain.VehicleEvent
import org.zirbes.eventsource.services.EventLogWriter

import ratpack.exec.Promise
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.http.TypedData
import ratpack.jackson.JsonRender

import static ratpack.jackson.Jackson.fromJson
import static ratpack.jackson.Jackson.json

@CompileStatic
@Slf4j
class EventHandler extends GroovyHandler {

    protected final HealthHandler health
    protected final EventLogWriter writer

    @Inject
    EventHandler(EventLogWriter writer, HealthHandler health) {
        this.health = health
        this.writer = writer
    }

    @Override
    protected void handle(GroovyContext context) {
        UUID key = UUID.randomUUID()
        LocalDateTime now = LocalDateTime.now()

        context.render(
            context.parse(fromJson(VehicleEvent)).map{ VehicleEvent event ->
                if (event?.data) {
                    // Overwrite event ID / date
                    event.id = key
                    event.date = now.toDate()
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
        )
    }

}
