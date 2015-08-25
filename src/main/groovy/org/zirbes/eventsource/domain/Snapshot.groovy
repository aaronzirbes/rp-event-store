package org.zirbes.eventsource.domain

import org.joda.time.LocalDateTime

abstract class Snapshot {

    UUID id
    String aggregateId
    Integer revision
    LocalDateTime time

    /** Json for now */
    byte[] data

}
