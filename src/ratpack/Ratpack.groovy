import org.zirbes.eventsource.handlers.EventHandler
import org.zirbes.eventsource.handlers.EventingErrorHandler
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
        module JacksonModule
    }

    handlers {
        get('health') { HealthHandler healthHandler ->
            context.insert(healthHandler)
        }
        post('event') { EventHandler eventHandler ->
            context.insert(eventHandler)
        }
    }
}
