package org.zirbes.eventsource

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.eventsource.handlers.HealthHandler
import org.zirbes.eventsource.handlers.EventHandler

import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain

@CompileStatic
@Slf4j
class EventSourceActionChain implements Action<Chain> {

    @Override
    void execute(Chain chain) throws Exception {
        Groovy.chain(chain) {
            get('health') { HealthHandler healthHandler ->
                context.insert(healthHandler)
            }
            post('event') { EventHandler eventHandler ->
                context.insert(eventHandler)
            }
        }
    }
}

