package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
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
