package org.zirbes.eventsource.domain

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
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
