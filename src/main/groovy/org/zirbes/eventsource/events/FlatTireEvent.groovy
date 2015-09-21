package org.zirbes.eventsource.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.domain.Bicycle

@Slf4j
@ToString
@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
class FlatTireEvent extends VehicleEvent {

    static final int PSI = 0
    String tire

    @Override
    void restoreData(Map data) {
        tire = data.tire
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.tires[tire].psi = PSI
        bicycle.tires[tire].holdsPressure = false
    }

}
