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
class PurchaseBikeEvent extends VehicleEvent {


    String make
    String model
    Integer wheelSize

    @Override
    void restoreData(Map data) {
        make = data.make
        model = data.model
        wheelSize = data.wheelSize
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.make = make
        bicycle.model = model
        bicycle.wheelSize = wheelSize
    }

}
