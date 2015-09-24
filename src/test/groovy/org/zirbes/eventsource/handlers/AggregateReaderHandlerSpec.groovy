package org.zirbes.eventsource.handlers

import org.zirbes.eventsource.aggregates.Bicycle
import org.zirbes.eventsource.services.AggregateReader

import ratpack.groovy.handling.GroovyContext

import rx.Observable

import ratpack.path.PathTokens
import ratpack.exec.Promise

import spock.lang.Specification

class AggregateReaderHandlerSpec extends Specification {

    HealthHandler health
    AggregateReader reader
    AggregateReaderHandler handler

    static final String AGGREGATE_ID = 'dd4cad36-85be-48b3-b2e1-51cc93712e4c'

    void setup() {
        health = Mock()
        reader = Mock()
        handler = new AggregateReaderHandler(reader, health)
    }

    void 'have context will render'() {
        given:
        UUID aggregateUuid = UUID.fromString(AGGREGATE_ID)
        GroovyContext context = Mock()
        PathTokens tokens = Mock()
        List<Bicycle> singleBike = [new Bicycle(id: aggregateUuid)]
        Observable observable = Observable.from(singleBike)

        when:
        handler.handle(context)

        then:
        1 * context.getPathTokens() >> tokens
        1 * tokens.get('aggregateId') >>  AGGREGATE_ID
        1 * context.render(_ as Promise)
        1 * reader.getAggregate(aggregateUuid) >> observable
        0 * _

    }

}
