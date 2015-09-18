package org.zirbes.eventsource.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class InflateTireEvent extends VehicleEvent {

    String tire
    int psi

    @Override
    void restoreData(Map data) {
        psi = data.psi as int
        tire = data.tire.toString()
    }

    @Override
    void process(Bicycle bicycle) {
        if (bicycle.tires[tire].holdsPressure) {
            bicycle.tires[tire].psi = psi
        }
    }

}
