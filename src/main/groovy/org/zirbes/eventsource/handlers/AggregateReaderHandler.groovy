package org.zirbes.eventsource.handlers

import com.google.inject.Inject

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.aggregates.Bicycle
import org.zirbes.eventsource.services.AggregateReader

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.jackson.Jackson.json
import static ratpack.rx.RxRatpack.promise

@CompileStatic
@Slf4j
class AggregateReaderHandler extends GroovyHandler {

    protected final HealthHandler health
    protected final AggregateReader reader

    @Inject
    AggregateReaderHandler(AggregateReader reader, HealthHandler health) {
        this.health = health
        this.reader = reader
    }

    @Override
    protected void handle(GroovyContext context) {
        UUID aggregateId = UUID.fromString(context.pathTokens.get('aggregateId'))

        context.render(
            promise(reader.getAggregate(aggregateId)).map{ List<Bicycle> events ->
                return json(events.first())
            }
        )
    }

}
