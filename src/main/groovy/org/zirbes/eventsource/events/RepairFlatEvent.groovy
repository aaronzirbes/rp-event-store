package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
class RepairFlatEvent extends VehicleEvent {

    String name

    @Override
    void restoreData(Map data) {
        name = data.name
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.tires[name].holdsPressure = true
    }

}
