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

    protected Observable<UUID> getAggregateObservableQueue() {
        return Observable.create({ Subscriber<UUID> subscriber ->
            while (!subscriber.unsubscribed) {
                try {
                    UUID uuid = queue.take()
                    subscriber.onNext(uuid)
                } catch (Throwable t) {
                    log.error "Unable to poll message: ${t.message}, retrying."
                }
            }
        } as Observable.OnSubscribe<UUID>).onBackpressureDrop()
    }

    void publish(UUID aggregateId) {
        queue.add(aggregateId)
    }

    @Override
    void onStart(StartEvent event) {
        log.info 'Aggregate publisher starting'

        getAggregateObservableQueue()
            .observeOn(Schedulers.io())
            .debounce(5, TimeUnit.SECONDS)
            .map({ UUID aggregateId -> return reader.getAggregate(aggregateId) })
            .cast(Aggregate)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Aggregate aggregate -> writer.writeAggregate(aggregate) },
                { Throwable t -> log.error "Error subscribing to aggregate writes." },
                { log.error "Subscription ended while observing aggregate publishing." }
            )
    }

    @Override
    void onStop(StopEvent event) {
        log.info 'Aggregate publisher stopping'
    }

}
