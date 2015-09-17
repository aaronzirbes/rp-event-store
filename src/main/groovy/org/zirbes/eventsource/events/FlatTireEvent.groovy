package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
class FlatTireEvent extends VehicleEvent {

    static final int PSI = 0
    String name

    @Override
    void restoreData(Map data) {
        name = data.name
    }

    @Override
    void process(Bicycle bicycle) {
        bicycle.tires[name].psi = PSI
        bicycle.tires[name].holdsPressure = false
    }

}
