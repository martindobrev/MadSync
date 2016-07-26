package com.maddob.sync.vertx.verticle;

import com.maddob.sync.MainTest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;

/**
 * Created by martindobrev on 19/02/16.
 */
public class DummySenderVerticle extends AbstractVerticle implements Handler<Long> {

    private int counter = 0;
    private final String address = "DUMMY_SENDER";


    @Override
    public void start() throws Exception {
        super.start();
        getVertx().setPeriodic(3000, this);
    }


    public void handle(Long event) {
        getVertx().eventBus().publish(MainTest.TestChannel, "Message " + counter);
        counter++;
    }
}
