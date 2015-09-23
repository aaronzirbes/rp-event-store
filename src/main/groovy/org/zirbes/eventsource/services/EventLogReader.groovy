package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.annotation.PostConstruct
import javax.inject.Inject

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.events.VehicleEvent

import rx.Observable

@Slf4j
class EventLogReader {

    protected static final String SELECT_ID = 'SELECT * FROM event WHERE aggregate_id = ? ALLOW FILTERING;'
    protected static final String SELECT_TIME_LT = '''SELECT * FROM event
                                                      WHERE aggregate_id = ?
                                                      AND time < ? ALLOW FILTERING;'''
    protected static final String SELECT_TIME_GT = '''SELECT * FROM event
                                                      WHERE aggregate_id = ?
                                                      AND time >= ? ALLOW FILTERING;'''
    protected static final String SELECT_TIME_BETWEEN = '''SELECT * FROM event
                                                           WHERE aggregate_id = ?
                                                           AND time < ?
                                                           AND time >= ? ALLOW FILTERING;'''


    protected final Session session
    protected final ObjectMapper objectMapper
    protected final TypeReference mapRef = new TypeReference<Map<String, Object>>() {}
    protected final TypeReference vehicleEventRef = new TypeReference<VehicleEvent>() {}

    protected BoundStatement events
    protected BoundStatement eventsUntil
    protected BoundStatement eventsSince
    protected BoundStatement windowOfEvents

    @Inject
    EventLogReader(Session session) {
        this.session = session
        this.objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
    }

    @PostConstruct
    void setup() {
        this.events = new BoundStatement(session.prepare(SELECT_ID))
        this.eventsUntil = new BoundStatement(session.prepare(SELECT_TIME_LT))
        this.eventsSince = new BoundStatement(session.prepare(SELECT_TIME_GT))
        this.windowOfEvents = new BoundStatement(session.prepare(SELECT_TIME_BETWEEN))
    }

    Observable<VehicleEvent> getEvents(UUID aggregateId) {
        BoundStatement bs = events.bind(aggregateId)
        return getEvents(session.execute(bs))
    }

    Observable<VehicleEvent> getEvents(UUID aggregateId, LocalDateTime startTime) {
        BoundStatement bs = eventsSince.bind(aggregateId, startTime.toDate())
        return getEvents(session.execute(bs))
    }

    Observable<VehicleEvent> getEventsUntil(UUID aggregateId, LocalDateTime endTime) {
        BoundStatement bs = eventsUntil.bind(aggregateId, endTime.toDate())
        return getEvents(session.execute(bs))
    }

    Observable<VehicleEvent> getEvents(UUID aggregateId, LocalDateTime startTime, LocalDateTime endTime) {
        BoundStatement bs = windowOfEvents.bind(
            aggregateId,
            startTime.toDate(),
            endTime.toDate()
        )
        return getEvents(session.execute(bs))
    }

    /** Use Jackson for now, we could use Apache Avro down the line **/
    protected Map<String, Object> mapFromData(String data) {
        if (data && data[0] == '{' && data[-1] == '}') {
            // TODO: Use ratpack.jackson.Jackson
            return objectMapper.readValue(data, mapRef)
        }
        return [:]
    }

    protected VehicleEvent vehicleFromRow(Row row) {
        ByteBuffer dataBytes = row.getBytes('data')
        String data = new String(dataBytes.array(), 'UTF-8')

        // Build a map
        Map<String, Object> eventMap = [
            id: row.getUUID('id'),
            revision: row.getInt('revision'),
            aggregateId: row.getUUID('aggregate_id'),
            date: row.getDate('time'),
            userId: row.getString('user_id'),
            clazz: row.getString('type'),
            dateEffective: row.getDate('date_effective')
        ]
        // Add the extra data
        eventMap << mapFromData(data)
        // TODO: Use ratpack.jackson.Jackson
        VehicleEvent event = objectMapper.convertValue(eventMap, vehicleEventRef)
        return event
    }

    protected Observable<VehicleEvent> getEvents(ResultSet rs) {
        return Observable.from(rs.all()).map({ Row row ->
            vehicleFromRow(row)
        }).cast(VehicleEvent)
    }

}
