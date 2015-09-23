package org.zirbes.eventsource.services

import groovy.util.logging.Slf4j

import javax.inject.Inject

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.aggregates.Bicycle
import org.zirbes.eventsource.events.VehicleEvent

import rx.Observable

@Slf4j
class AggregateReader {

    protected final EventLogReader eventLogReader

    @Inject
    AggregateReader(EventLogReader eventLogReader) {
        this.eventLogReader = eventLogReader
    }

    Observable<Bicycle> getAggregate(UUID aggregateId) {
        // TODO: Read from snapshot first
        return aggregate(aggregateId, eventLogReader.getEvents(aggregateId))

    }

    Observable<Bicycle> getAggregateAsOf(UUID aggregateId, LocalDateTime endTime) {
        // TODO: Read from snapshot first
        return aggregate(aggregateId, eventLogReader.getEventsUntil(aggregateId, endTime))
    }

    protected Observable<VehicleEvent> aggregate(UUID aggregateId, Observable<VehicleEvent> observable) {
        Bicycle initalState = new Bicycle(id: aggregateId)
        // rx.reduce ~= groovy.inject
        observable.reduce(initalState,
        { Bicycle bicycle, VehicleEvent event ->
            bicycle.applyChange(event)
            bicycle.markEventsAsCommitted()
            return bicycle
        })
    }


}
