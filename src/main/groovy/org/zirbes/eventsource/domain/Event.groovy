package org.zirbes.eventsource.domain

import org.joda.time.LocalDateTime

class Event {

    UUID eventId
    LocalDateTime time

    String eventType
    String userId
    String sourceSystem

    /** Json for now */
    byte[] data

}
