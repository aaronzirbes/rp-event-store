package org.zirbes.eventsource.services

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session

import java.nio.ByteBuffer

import org.zirbes.eventsource.JsonSpecification
import org.zirbes.eventsource.events.AdjustSeatHeightEvent
import org.zirbes.eventsource.events.FlatTireEvent
import org.zirbes.eventsource.events.InflateTireEvent
import org.zirbes.eventsource.events.LockBikeEvent
import org.zirbes.eventsource.events.PurchaseBikeEvent
import org.zirbes.eventsource.events.RepairFlatEvent
import org.zirbes.eventsource.events.RideBikeEvent
import org.zirbes.eventsource.events.TurnOffLightEvent
import org.zirbes.eventsource.events.TurnOnLightEvent
import org.zirbes.eventsource.events.UnockBikeEvent
import org.zirbes.eventsource.events.VehicleEvent

import spock.lang.Unroll

class EventLogReaderSpec extends JsonSpecification {

    EventLogReader reader
    Session session

    BoundStatement events
    BoundStatement eventsUntil
    BoundStatement eventsSince
    BoundStatement windowOfEvents

    void setup() {
        events = Mock()
        eventsUntil = Mock()
        eventsSince = Mock()
        windowOfEvents = Mock()

        session = Mock()
        reader = new EventLogReader(session)
        reader.events = events
        reader.eventsUntil = eventsUntil
        reader.eventsSince = eventsSince
        reader.windowOfEvents = windowOfEvents
    }

    @Unroll
    void 'can get data from #fixture event'() {
        given:
        Row row = Mock()
        String json = jsonFromFixture(fixture)
        VehicleEvent reference = objectMapper.readValue(json, VehicleEvent)

        when:
        VehicleEvent event = reader.vehicleFromRow(row)

        then:
        event.class == clazz
        1 * row.getBytes('data') >> ByteBuffer.wrap(jsonData.bytes)
        1 * row.getUUID('id') >> UUID.randomUUID()
        1 * row.getInt('revision') >> reference.revision
        1 * row.getUUID('aggregate_id') >> reference.aggregateId
        1 * row.getDate('time') >> reference.date
        1 * row.getString('user_id') >> reference.userId
        1 * row.getString('type') >> reference.clazz
        1 * row.getDate('date_effective') >> reference.dateEffective
        0 * _

        where:
        fixture                 | clazz                 | jsonData
        'adjustSeatHeightEvent' | AdjustSeatHeightEvent | '{"height":12}'
        'flatTireEvent'         | FlatTireEvent         | '{"tire":"front"}'
        'inflateTireEvent'      | InflateTireEvent      | '{"psi":80,"tire":"rear"}'
        'lockBikeEvent'         | LockBikeEvent         | ''
        'purchaseBikeEvent'     | PurchaseBikeEvent     | '{"make":"Surly","model":"Steamroller","wheelSize":700}'
        'repairFlatEvent'       | RepairFlatEvent       | '{"tire":"rear"}'
        'rideBikeEvent'         | RideBikeEvent         | '{"headingDegrees":0,"lat":45.003966,"lon":-93.247044,"velocity":12.0}'
        'turnOnLightEvent'      | TurnOnLightEvent      | '{"light":"front"}'
        'turnOffLightEvent'     | TurnOffLightEvent     | '{"light":"front"}'
        'unockBikeEvent'        | UnockBikeEvent        | ''
    }

}
