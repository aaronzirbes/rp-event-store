package org.zirbes.eventsource.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.aggregates.Bicycle

@Slf4j
@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class TurnOnLightEvent extends VehicleEvent {

    static final boolean ON = true

    String light

    @Override
    void restoreData(Map data) {
        light = data.light.toString()
    }

    @Override
    void process(Bicycle bicycle) {
        if (bicycle.lights[light].battery > 0) {
            bicycle.lights[light].on = ON
        }
    }

}
