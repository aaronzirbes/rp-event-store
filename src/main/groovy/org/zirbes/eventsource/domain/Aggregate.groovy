package org.zirbes.eventsource.domain

abstract class Aggregate {

    String id
    Integer revision
    String type

    abstract void applyNew(List<Event> events)
    abstract void loadHistorical(List<Event> events)
    abstract void loadFromSnapshot(Snapshot snapshot)
}

