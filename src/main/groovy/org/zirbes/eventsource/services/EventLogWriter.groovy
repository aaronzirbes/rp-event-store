package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session

import groovy.util.logging.Slf4j
import ratpack.exec.Promise

import javax.inject.Inject

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.domain.VehicleEvent

import java.nio.ByteBuffer

@Slf4j
class EventLogWriter {

    protected final Session session
    protected final PreparedStatement insert

    @Inject
    EventLogWriter(Session session) {
        this.session = session
        this.insert = session.prepare('''
            INSERT INTO event (
                id,
                revision,
                type,
                aggregate_id,
                time,
                user_id,
                date_effective,
                source_system,
                data
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            '''
        )
    }

    void writeEvent(VehicleEvent event) {
        insertData(event)
    }

    void insertData(VehicleEvent event) {
        Promise.of{
            BoundStatement bs = new BoundStatement(insert).bind(
                    event.id.toString(),
                    event.revision,
                    VehicleEvent.simpleName,
                    event.aggregateId.toString(),
                    event.date,
                    event.userId,
                    event.dateEffective,
                    event.sourceSystem,
                    ByteBuffer.wrap(event.data.bytes)
            )
            session.execute(bs)
            log.info 'Wrote key={} date={} event={}', event.id, event.date, event.data
        }.then{}

    }

}
