package org.zirbes.eventsource.services

import groovy.util.logging.Slf4j

@Slf4j
class EventLogWriter {

    void writeEvent(Object ojb) {
        log.info "writing event"
    }

}
