Ratpack Event Store POC
-----------------------------

Writing an event store proof of concept.

# Goals

Event Sourcing model:

* Event
* Snapshot
* Aggregate


# TODOs

Event Store: Cassandra (embedded)
Aggregate Store: Elasticsearch (embedded)

Apache Avro for model storage and aggregate compatibility checks

# Demo Model

Events:
 * Ride / Riding / Stop
 * Lock / Unlock
 * Turn on light(s)
 * Turn off light(s)
 * Inflate tire
 * Pressure Check

Aggregate: Bicycle
 * Location
 * Speed
 * Direction
 * Front Light status / battery
 * Rear Light status / battery
 * Lock status
 * Tire Pressure

