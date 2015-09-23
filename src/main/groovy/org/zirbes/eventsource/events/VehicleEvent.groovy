package org.zirbes.eventsource.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.thirdchannel.eventsource.AbstractEvent

import groovy.transform.ToString

import org.zirbes.eventsource.domain.Bicycle

@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
@JsonTypeInfo(
    use=JsonTypeInfo.Id.CLASS,
    include=JsonTypeInfo.As.PROPERTY,
    property='clazz')
// TODO: Abstract
class VehicleEvent extends AbstractEvent<Bicycle> {

    @Override
    void setClazz(String clazz) { }

    @Override
    @JsonProperty('clazz')
    String getClazz() { getClass().name }

    @Override
    void restoreData(Map data) { }

    @Override
    void process(Bicycle bicycle) { }

}
