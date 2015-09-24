package org.zirbes.eventsource.services

import com.thirdchannel.eventsource.Aggregate

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import rx.schedulers.Schedulers

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

import javax.inject.Inject

import ratpack.server.Service
import ratpack.server.StartEvent
import ratpack.server.StopEvent

import rx.Observable
import rx.Subscriber

import java.util.concurrent.TimeUnit

@CompileStatic
@Slf4j
class AggregatePublisher implements Service {

    static final Integer DEBOUNCE_SECONDS = 2

    protected final AggregateReader reader
    protected final AggregateWriter writer
    protected final BlockingQueue<UUID> queue

    @Inject
    AggregatePublisher(AggregateReader reader, AggregateWriter writer) {
        this.reader = reader
        this.writer = writer
        this.queue = new LinkedBlockingQueue<UUID>()
    }

    BlockingQueue<UUID> queue = new LinkedBlockingQueue<UUID>()

    void publish(UUID aggregateId) {
        queue.add(aggregateId)
    }

    @Override
    void onStart(StartEvent event) {
        log.info 'Aggregate publisher starting'

        getAggregateObservableQueue()
            .onBackpressureDrop()
            .observeOn(Schedulers.io())
            .debounce(DEBOUNCE_SECONDS, TimeUnit.SECONDS)
            .flatMap({ UUID aggregateId -> return reader.getAggregate(aggregateId) })
            .cast(Aggregate)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Aggregate aggregate -> writer.writeAggregate(aggregate) },
                { Throwable t -> log.error "Error subscribing to aggregate writes.", t },
                { log.error "Subscription ended while observing aggregate publishing." }
            )
    }

    @Override
    void onStop(StopEvent event) {
        log.info 'Aggregate publisher stopping'
    }

    protected Observable<UUID> getAggregateObservableQueue() {
        return Observable.create({ Subscriber<UUID> subscriber ->
            while (!subscriber.unsubscribed) {
                try {
                    UUID uuid = queue.take()
                    log.info 'pulled aggregateId={}', uuid
                    subscriber.onNext(uuid)
                } catch (InterruptedException ex) {
                    log.warn "Interrupted while polling message queue, retrying."
                } catch (Throwable t) {
                    log.error "Unable to poll message: ${t.message}, retrying.", t
                }
            }
        } as Observable.OnSubscribe<UUID>)
    }

}
