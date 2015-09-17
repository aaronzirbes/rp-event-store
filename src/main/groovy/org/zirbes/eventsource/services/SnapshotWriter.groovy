package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session

import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.inject.Inject

import org.zirbes.eventsource.domain.BicycleSnapshot

import ratpack.exec.Promise

@Slf4j
class SnapshotWriter {

    protected final Session session
    protected final PreparedStatement insert

    @Inject
    SnapshotWriter(Session session) {
        this.session = session
        this.insert = session.prepare('''
            INSERT INTO snapshot (
                id,
                aggregate_id,
                revision,
                time,
                data
            ) VALUES (?, ?, ?, ?, ?);
            '''
        )
    }

    void writeSnapshot(BicycleSnapshot snapshot) {
        insertData(snapshot)
    }

    void insertData(BicycleSnapshot snapshot) {
        Promise.of{
            BoundStatement bs = new BoundStatement(insert).bind(
                    snapshot.id,
                    snapshot.aggregateId,
                    snapshot.revision,
                    snapshot.date,
                    ByteBuffer.wrap(snapshot.data.bytes)
            )
            session.execute(bs)
            log.info(
                'Wrote key={} aggregateId={} date={} snapshot={}',
                snapshot.id, snapshot.aggregateId, snapshot.date, snapshot.data
            )
        }.then{}

    }

}
