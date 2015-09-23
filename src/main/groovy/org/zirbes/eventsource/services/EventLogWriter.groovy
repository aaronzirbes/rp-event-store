package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Session
import com.thirdchannel.eventsource.AbstractEvent

import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.annotation.PostConstruct
import javax.inject.Inject

import ratpack.exec.Promise

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
                                              )
                                              VALUES (?, ?, ?, ?, ?, ?, ?, ?);'''

    protected static final List<String> BASE_FIELDS = [
        'id',
        'clazz',
        'revision',
        'aggregateId',
        'date',
        'userId',
        'dateEffective'
    ].asImmutable()

    @Override
    protected String getInsertStatement() { INSERT }

    @Override
    protected List<String> getBaseFields() { BASE_FIELDS }

    @Inject
    EventLogWriter(Session session) {
        super(session)
    }

    @PostConstruct
    void setup() {
        super.setup()
    }

    void writeEventAsync(AbstractEvent event) {
        Promise.of{ writeEvent(event) }.then{}
    }

    protected void writeEvent(AbstractEvent event) {
        BoundStatement bs = insert.bind(
            event.id,
            event.clazz,
            event.revision,
            event.aggregateId,
            event.date,
            event.userId,
            event.dateEffective,
            ByteBuffer.wrap(dataFromAbstract(event))
        )
        session.execute(bs)
        log.info 'Wrote key={} date={} event={}', event.id, event.date, event.data
    }

}
