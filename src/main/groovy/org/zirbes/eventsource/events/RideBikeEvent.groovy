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
class RideBikeEvent extends VehicleEvent {

    BigDecimal lat
    BigDecimal lon
    Integer headingDegrees
    BigDecimal velocity

    @Override
    void restoreData(Map data) {
        lat = data.lat
        lon = data.lon
        headingDegrees = data.headingDegrees
        velocity = data.velocity
    }

    @Override
    void process(Bicycle bicycle) {
        if (!bicycle.locked) {
            bicycle.location.lat = lat
            bicycle.location.lon = lon
            bicycle.headingDegrees = headingDegrees
            bicycle.velocity = velocity
        }
    }

}
