package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session

import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.annotation.PostConstruct
import javax.inject.Inject

import org.zirbes.eventsource.domain.BicycleSnapshot
import com.thirdchannel.eventsource.Snapshot

import ratpack.exec.Promise

@Slf4j
class SnapshotWriter extends AbstractWriter {

    protected static final String INSERT = '''INSERT INTO snapshot (
                                                  id,
                                                  type,
                                                  aggregate_id,
                                                  revision,
                                                  time,
                                                  data
                                              ) VALUES (?, ?, ?, ?, ?);'''

    protected static final List<String> BASE_FIELDS = [
        'id',
        'clazz',
        'revision',
        'aggregateId',
        'date'
    ].asImmutable()

    @Inject
    SnapshotWriter(Session session) {
        super(session)
    }

    @PostConstruct
    void setup() {
        super.setup()
    }

    @Override
    protected String getInsertStatement() { INSERT }

    @Override
    protected List<String> getBaseFields() { BASE_FIELDS }

    void writeSnapshotAsync(Snapshot snapshot) {
        Promise.of{ writeSnapshot(snapshot) }.then{}
    }

    protected void writeSnapshot(Snapshot snapshot) {
        // TODO: Vette this out
        BoundStatement bs = insert.bind(
            snapshot.id,
            snapshot.clazz,
            snapshot.aggregateId,
            snapshot.revision,
            snapshot.date,
            ByteBuffer.wrap(dataFromEvent(snapshot))
        )
        session.execute(bs)
        log.info 'Wrote key={} date={} snapshot={}', snapshot.id, snapshot.date, snapshot.data
    }

}
