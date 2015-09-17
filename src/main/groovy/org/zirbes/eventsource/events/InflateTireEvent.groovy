package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
class InflateTireEvent extends VehicleEvent {

    String name
    int psi

    @Override
    void restoreData(Map data) {
        psi = data.psi as int
        name = data.name.toString()
    }

    @Override
    void process(Bicycle bicycle) {
        if (bicycle.tires[name].holdsPressure) {
            bicycle.tires[name].psi = psi
        }
    }

}
