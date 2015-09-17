package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
class LockBikeEvent extends VehicleEvent {

    static final LOCKED = true

    @Override
    void restoreData(Map data) {
        locked = LOCKED
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.locked = LOCKED
    }

}
