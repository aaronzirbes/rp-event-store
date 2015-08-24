package org.zirbes.eventsource.handlers

import groovy.transform.CompileStatic

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import java.util.concurrent.atomic.AtomicInteger

import static ratpack.jackson.Jackson.json

@CompileStatic
class HealthHandler extends GroovyHandler {

    AtomicInteger status = new AtomicInteger(200)

    @Override
    protected void handle(GroovyContext context) {
        Integer currentStatus = status.get()
        context.response.status(currentStatus)
        if (currentStatus == 200) {
            context.render(json([status: 'UP']))
        } else {
            context.render(json([status: 'FAILING']))
        }
    }

}
