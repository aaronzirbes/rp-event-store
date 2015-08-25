package org.zirbes.eventsource.domain

import org.joda.time.LocalDateTime

abstract class Event {

    UUID id
    Integer revision
    String type
    String aggregateId
    LocalDateTime time

    String userId
    String sourceSystem

    /** Json for now */
    byte[] data

    abstract void loadDataFromString(String str)
    abstract Aggregate process()
}
