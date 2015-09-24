package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSetFuture
import com.datastax.driver.core.Session
import com.thirdchannel.eventsource.AbstractEvent

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.annotation.PostConstruct
import javax.inject.Inject

import ratpack.exec.Promise

@CompileStatic
@Slf4j
class EventLogWriter extends AbstractWriter {

    protected static final String INSERT = '''INSERT INTO event (
                                                  id,
                                                  type,
                                                  revision,
                                                  aggregate_id,
                                                  time,
                                                  user_id,
                                                  date_effective,
                                                  data
                                              ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)'''

    protected static final List<String> BASE_FIELDS = [
        'id',
        'clazz',
        'revision',
        'aggregateId',
        'date',
        'userId',
        'dateEffective'
    ].asImmutable()

    protected final AggregatePublisher publisher

    @Override
    protected String getInsertStatement() { INSERT }

    @Override
    protected List<String> getBaseFields() { BASE_FIELDS }

    @Inject
    EventLogWriter(AggregatePublisher publisher, Session session) {
        super(session)
        this.publisher = publisher
        this.insert = new BoundStatement(session.prepare(insertStatement))
    }

    protected void writeEvent(AbstractEvent event) {
        byte[] data = dataFromAbstract(event)
        UUID aggregateId = event.aggregateId
        ByteBuffer byteBuffer = ByteBuffer.wrap(data)
        assert insert != null
        BoundStatement bs = insert.bind(
            event.id,
            event.clazz,
            event.revision,
            aggregateId,
            event.date,
            event.userId,
            event.dateEffective,
            byteBuffer
        )
        session.executeAsync(bs)
        log.info 'Wrote key={} date={} aggregateId={}', event.id, event.date, aggregateId
        publisher.publish(aggregateId)
    }

}
