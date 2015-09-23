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
class AdjustSeatHeightEvent extends VehicleEvent {

    int height

    @Override
    void restoreData(Map data) {
        height = data.height as int
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.seatHeight = height
    }

}
