package org.zirbes.eventsource.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
class RepairFlatEvent extends VehicleEvent {

    String tire

    @Override
    void restoreData(Map data) {
        tire = data.tire
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.tires[tire].holdsPressure = true
    }

}
