package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session

import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.inject.Inject

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.events.VehicleEvent

import rx.Observable

@Slf4j
class EventLogReader {

    protected final Session session
    protected final PreparedStatement events
    protected final PreparedStatement eventsUntil
    protected final PreparedStatement eventsSince
    protected final PreparedStatement windowOfEvents

    @Inject
    EventLogReader(Session session) {
        this.session = session

        // Start dates are inclusive
        // End dates are exclusive

        this.events = session.prepare('SELECT * FROM event WHERE aggregate_id = ? ALLOW FILTERING;')

        this.eventsUntil = session.prepare('''SELECT * FROM event
                                               WHERE aggregate_id = ?
                                               AND time < ? ALLOW FILTERING;''')

        this.eventsSince = session.prepare('''SELECT * FROM event
                                               WHERE aggregate_id = ?
                                               AND time >= ? ALLOW FILTERING;''')

        this.windowOfEvents = session.prepare('''SELECT * FROM event
                                                 WHERE aggregate_id = ?
                                                 AND time < ?
                                                 AND time >= ? ALLOW FILTERING;''')
    }

    Observable<VehicleEvent> getEvents(UUID aggregateId) {
        BoundStatement bs = new BoundStatement(events).bind(aggregateId)
        return getEvents(session.execute(bs))
    }

    Observable<VehicleEvent> getEvents(UUID aggregateId, LocalDateTime startTime) {
        BoundStatement bs = new BoundStatement(eventsSince).bind(aggregateId, startTime.toDate())
        return getEvents(session.execute(bs))
    }

    Observable<VehicleEvent> getEventsUntil(UUID aggregateId, LocalDateTime startTime) {
        BoundStatement bs = new BoundStatement(eventsUntil).bind(aggregateId, startTime.toDate())
        return getEvents(session.execute(bs))
    }

    Observable<VehicleEvent> getEvents(UUID aggregateId, LocalDateTime startTime, LocalDateTime endTime) {
        BoundStatement bs = new BoundStatement(windowOfEvents).bind(
            aggregateId,
            startTime.toDate(),
            endTime.toDate()
        )
        return getEvents(session.execute(bs))
    }

    protected Observable<VehicleEvent> getEvents(ResultSet rs) {
        return Observable.from(rs.all()).map({ Row row ->
            ByteBuffer dataBytes = row.getBytes('data')
            String data = new String(dataBytes.array(), 'UTF-8')
            return new VehicleEvent(
                id: row.getUUID('id'),
                revision: row.getInt('revision'),
                aggregateId: row.getUUID('aggregate_id'),
                date: row.getDate('time'),
                data: data,
                userId: row.getString('user_id'),
                dateEffective: row.getDate('date_effective')
            )
        }).cast(VehicleEvent)
    }

}
