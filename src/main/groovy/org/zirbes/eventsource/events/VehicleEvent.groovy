package org.zirbes.eventsource.domain

import com.thirdchannel.eventsource.AbstractEvent
import groovy.transform.ToString

@ToString
class VehicleEvent extends AbstractEvent<Bicycle> {

    @Override
    void restoreData(Map data) { }

    @Override
    void process(Bicycle bicycle) { }

}
