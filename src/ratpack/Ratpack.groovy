import ratpack.groovy.template.MarkupTemplateModule

import org.zirbes.eventsource.handlers.EventingErrorHandler
import org.zirbes.eventsource.modules.EventSourceModule
import org.zirbes.eventsource.EventSourceActionChain

import ratpack.config.ConfigData
import ratpack.error.ServerErrorHandler
import ratpack.jackson.guice.JacksonModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        port 8080
    }
    bindings {
        ConfigData configData = ConfigData.of()
                .yaml(ClassLoader.getSystemResource('application.yml'))
                .env()
                .sysProps()
                .build()
        bindInstance(ConfigData, configData)
        bindInstance(ServerErrorHandler, new EventingErrorHandler())
        add new EventSourceModule(configData.get('/cassandra', Object))
        add JacksonModule
    }

    handlers {
        handler chain(registry.get(EventSourceActionChain))
    }
}
