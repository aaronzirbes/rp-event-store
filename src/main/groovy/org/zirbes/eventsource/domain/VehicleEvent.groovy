package org.zirbes.eventsource.domain

import com.thirdchannel.eventsource.AbstractEvent
import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString
class VehicleEvent extends AbstractEvent<VehicleAggregate> {

    @Override
    void restoreData(Map data) {
        log.info "TODO: restoreData"
    }

    @Override
    void process(VehicleAggregate aggregate) {
        log.info "TODO: process"
    }

    static String getType() {
        this.class.simpleName
    }
}
