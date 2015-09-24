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
class RepairFlatEvent extends VehicleEvent {

    static final boolean HOLDS_PRESSURE = true

    String tire

    @Override
    void restoreData(Map data) {
        tire = data.tire
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.tires[tire].holdsPressure = HOLDS_PRESSURE
    }

}
