package com.maddob.sync;

import com.maddob.sync.vertx.verticle.DummyReceiverVerticle;
import com.maddob.sync.vertx.verticle.DummySenderVerticle;
import com.maddob.sync.vertx.ws.server.WebSocketServerVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * Test class to run some manual tests
 *
 * The class shall not be used in production!!!
 *
 * Created by martindobrev on 16/02/16.
 */
public class MainTest {

	public static final String TestChannel = "com.eu.dobrev.hellovertx";

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		DummyReceiverVerticle receiver = new DummyReceiverVerticle();
		// DummySenderVerticle sender = new DummySenderVerticle();
		vertx.deployVerticle(receiver);

		WebSocketServerVerticle webSocketServerVerticle = new WebSocketServerVerticle();
		vertx.deployVerticle(webSocketServerVerticle);


	}
}
