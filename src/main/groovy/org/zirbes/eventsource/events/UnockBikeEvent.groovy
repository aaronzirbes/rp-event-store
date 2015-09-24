package org.zirbes.eventsource.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.aggregates.Bicycle

@Slf4j
@ToString
@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
class UnockBikeEvent extends VehicleEvent {

    static final LOCKED = false

    @Override
    void restoreData(Map data) {
        locked = LOCKED
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.locked = LOCKED
    }

}
