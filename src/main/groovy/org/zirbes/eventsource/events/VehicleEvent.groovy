package org.zirbes.eventsource.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.thirdchannel.eventsource.AbstractEvent

import groovy.transform.ToString

@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class VehicleEvent extends AbstractEvent<Bicycle> {

    @Override
    void restoreData(Map data) { }

    @Override
    void process(Bicycle bicycle) { }

}
