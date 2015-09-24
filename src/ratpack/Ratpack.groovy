import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule

import org.zirbes.eventsource.handlers.EventWriterHandler
import org.zirbes.eventsource.handlers.EventReaderHandler
import org.zirbes.eventsource.handlers.EventingErrorHandler
import org.zirbes.eventsource.handlers.AggregateReaderHandler
import org.zirbes.eventsource.handlers.HealthHandler
import org.zirbes.eventsource.modules.EventSourceModule

import ratpack.config.ConfigData
import ratpack.error.ServerErrorHandler
import ratpack.jackson.guice.JacksonModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        port 8080
    }
    bindings {
        ConfigData configData = ConfigData.of { config ->
                config.yaml(ClassLoader.getSystemResource('application.yml'))
                config.env()
                config.sysProps()
                config.build()
        }
        bindInstance(ConfigData, configData)
        bindInstance(ServerErrorHandler, new EventingErrorHandler())
        module (new EventSourceModule(configData.get('/cassandra', Object)))
        module JacksonModule, { config ->
            config.modules(new JodaModule())
            config.withMapper{ ObjectMapper objectMapper ->
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false)
                objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true)
            }
        }
    }

    handlers {
        get('health') { HealthHandler handler -> context.insert(handler) }
        post('events') { EventWriterHandler handler -> context.insert(handler) }
        get('events/:aggregateId') { EventReaderHandler handler -> context.insert(handler) }
        get('aggregate/:aggregateId') { AggregateReaderHandler handler -> context.insert(handler) }
    }
}

/* http get :8080/events/7b94510d-fd9e-4d4b-9a93-127a83f7fd12 */
