package org.zirbes.eventsource.handlers

import com.google.inject.Inject

import com.thirdchannel.eventsource.AbstractEvent

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.services.EventLogReader
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.rx.RxRatpack.promise

import static ratpack.jackson.Jackson.json

@CompileStatic
@Slf4j
class EventReaderHandler extends GroovyHandler {

    protected final HealthHandler health
    protected final EventLogReader reader

    @Inject
    EventReaderHandler(EventLogReader reader, HealthHandler health) {
        this.health = health
        this.reader = reader
    }

    @Override
    protected void handle(GroovyContext context) {
        UUID aggregateId = UUID.fromString(context.pathTokens.get('aggregateId'))
        context.render(
            promise(reader.getEvents(aggregateId)).map{ List<AbstractEvent> events ->
                return json(events)
            }
        )
    }

}
