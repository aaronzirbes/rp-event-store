package org.zirbes.eventsource.handlers

import com.google.inject.Inject

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import ratpack.exec.Promise
import ratpack.http.TypedData

import java.nio.ByteBuffer

import org.joda.time.LocalDateTime
import org.zirbes.eventsource.services.EventLogWriter

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.jackson.Jackson.json

@CompileStatic
@Slf4j
class EventHandler extends GroovyHandler {

    protected final HealthHandler health
    protected final EventLogWriter writer

    @Inject
    EventHandler(EventLogWriter writer, HealthHandler health) {
        this.health = health
        this.writer = writer
    }

    @Override
    protected void handle(GroovyContext context) {
        String key = "${UUID.randomUUID()}:${LocalDateTime.now()}"
        String message = 'queued'
        int status = 204

        //byte[] bytes = context.request.body.bytes
        Promise<TypedData> bodyPromise = context.request.body
        bodyPromise.onError{ Throwable t ->
            log.error 'Unable to read message data: {}', t.message
        }.then{ TypedData body ->
            byte[] bytes = body.bytes

            if (bytes.size() == 0) {
                message = 'empty request'
            } else {
                status = 201
                String byteText = new String(bytes, 'UTF-8')
                log.trace "posting data: ${byteText}"

                log.info 'Got data: {}, but got nuthin to do with it yet', bytes
                // TODO: use rx.Observable to write to Cassandra
            }

            context.response.status(status)
            context.render(json([record: key, message: message]))
        }
    }

}
