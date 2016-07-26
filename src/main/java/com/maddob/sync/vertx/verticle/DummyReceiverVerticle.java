package com.maddob.sync.vertx.verticle;

import com.maddob.sync.MainTest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * Created by martindobrev on 19/02/16.
 */
public class DummyReceiverVerticle extends AbstractVerticle implements Handler<Message<String>> {

    private MessageConsumer<String> consumer;

    @Override
    public void start() throws Exception {
        super.start();
        EventBus eventBus = getVertx().eventBus();
        consumer = eventBus.consumer(MainTest.TestChannel);
        consumer.handler(this);
    }

    @Override
    public void stop() throws Exception {
        consumer.unregister();
        super.stop();
    }

    @Override
    public void handle(Message<String> event) {
        System.out.println(event.body());
    }
}
