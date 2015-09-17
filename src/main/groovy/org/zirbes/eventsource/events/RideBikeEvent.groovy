package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
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
