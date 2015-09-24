# Ratpack Event Store POC

*Work in Progress*

Writing an event store proof of concept.

# Goals

Event Sourcing model:

* Event
* Snapshot
* Aggregate

## TODOs

* Write events from JSON to Cassandra
* Load Events from Cassandra
* Build Aggregate from events loaded from Cassandra

## Nice to have TODOs
* Apache Avro for model storage and aggregate compatibility checks?

## Demo Model

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

## Getting Setup

Running Cassandra in a docker container

    docker pull cassandra:2.2
    docker run --name evsrc-cassandra -p 9042:9042 -d cassandra:2.2

Running Elasticsearch in a docker container

    docker pull elasticsearch:1.7
    docker run --name evsrc-elasticsearch -p 9200:9200 -p 9300:9300 -p 54328:54328 -d elasticsearch:1.7



