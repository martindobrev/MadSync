package com.maddob.sync;

import com.maddob.sync.vertx.verticle.DummyReceiverVerticle;
import com.maddob.sync.vertx.verticle.DummySenderVerticle;
import io.vertx.core.Vertx;

/**
 * Created by martindobrev on 16/02/16.
 */
public class MainTest {

	public static final String TestChannel = "com.eu.dobrev.hellovertx";

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		DummyReceiverVerticle receiver = new DummyReceiverVerticle();
		DummySenderVerticle sender = new DummySenderVerticle();
		vertx.deployVerticle(receiver);
		vertx.deployVerticle(sender);
	}
}
