package com.maddob.test.sync;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.CaseInsensitiveHeaders;

/**
 * Created by martindobrev on 15/03/16.
 */

public class TestMessage implements Message<String> {

    private String body;
    private MultiMap headers;


    public TestMessage() {}


    public TestMessage(String body) {
        this.body = body;
    }

    @Override
    public String address() {
        return null;
    }

    @Override
    public MultiMap headers() {
        return headers;
    }

    @Override
    public String body() {
        return null;
    }

    @Override
    public String replyAddress() {
        return null;
    }

    @Override
    public void reply(Object message) {
    }

    @Override
    public <R> void reply(Object message, Handler<AsyncResult<Message<R>>> replyHandler) {

    }

    @Override
    public void reply(Object message, DeliveryOptions options) {

    }

    @Override
    public <R> void reply(Object message, DeliveryOptions options, Handler<AsyncResult<Message<R>>> replyHandler) {

    }

    @Override
    public void fail(int failureCode, String message) {

    }

    public void setHeaders(MultiMap headers) {
        this.headers = headers;
    }


    public void addAll(MultiMap headersToAdd) {
        if (null == headers) {
            headers = new CaseInsensitiveHeaders();
        }
        headers.addAll(headersToAdd);
    }

    public void add(String header, String value) {
        if (null == headers) {
            headers = new CaseInsensitiveHeaders();
        }
        headers.add(header, value);
    }
}

