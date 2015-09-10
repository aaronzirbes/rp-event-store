package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session

import com.thirdchannel.eventsource.AbstractEvent

import groovy.util.logging.Slf4j
import ratpack.exec.Promise

import javax.inject.Inject

import org.joda.time.LocalDateTime

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
                aggregate_id,
                time,
                user_id,
                date_effective,
                data
            )
            VALUES (?, ?, ?, ?, ?, ?, ?);
            '''
        )
    }

    void writeEvent(AbstractEvent event) {
        Promise.of{
            BoundStatement bs = new BoundStatement(insert).bind(
                    event.id,
                    event.revision,
                    event.aggregateId,
                    event.date,
                    event.userId,
                    event.dateEffective,
                    ByteBuffer.wrap(event.data.bytes)
            )
            session.execute(bs)
            log.info 'Wrote key={} date={} event={}', event.id, event.date, event.data
        }.then{}

    }

}
