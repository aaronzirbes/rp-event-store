package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session

import org.zirbes.eventsource.JsonSpecification
import org.zirbes.eventsource.events.VehicleEvent

import spock.lang.Unroll

class EventLogWriterSpec extends JsonSpecification {

    EventLogWriter writer
    Session session
    BoundStatement insert

    void setup() {
        session = Mock()
        insert = Mock()
        writer = new EventLogWriter(session)
        writer.insert = insert
    }

    @Unroll
    void 'can get data from #fixture event'() {
        given:
        String json = jsonFromFixture(fixture)
        VehicleEvent event = objectMapper.readValue(json, VehicleEvent)

        when:
        byte[] bytes = writer.dataFromAbstract(event)
        String data = new String(bytes, 'UTF-8')

        then:
        data == jsonData
        0 * _

        when:
        writer.writeEvent(event)

        then:
        1 * insert.bind(_) >> insert
        1 * session.execute(insert)
        0 * _

        where:
        fixture                 | jsonData
        'adjustSeatHeightEvent' | '{"height":12}'
        'flatTireEvent'         | '{"tire":"front"}'
        'inflateTireEvent'      | '{"psi":80,"tire":"rear"}'
        'lockBikeEvent'         | ''
        'purchaseBikeEvent'     | '{"make":"Surly","model":"Steamroller","wheelSize":700}'
        'repairFlatEvent'       | '{"tire":"rear"}'
        'rideBikeEvent'         | '{"headingDegrees":0,"lat":45.003966,"lon":-93.247044,"velocity":12.0}'
        'turnOnLightEvent'      | '{"light":"front"}'
        'turnOffLightEvent'     | '{"light":"front"}'
        'unockBikeEvent'        | ''

    }

}
