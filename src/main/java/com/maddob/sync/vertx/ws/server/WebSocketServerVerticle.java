package com.maddob.sync.vertx.ws.server;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * WebSocket server verticle for
 * testing the message provider and consumer through web-sockets
 *
 * Created by martindobrev on 10/10/16.
 */
public class WebSocketServerVerticle extends AbstractVerticle {


    private HttpServer server;

    @Override
    public void start() throws Exception {
        super.start();

        server = getVertx().createHttpServer();

        Router router = Router.router(getVertx());

        MessageConsumer<JsonObject> jsonMessageConsumer = getVertx().eventBus().consumer("login");
        jsonMessageConsumer.handler(jsonMessage -> {
            System.out.println("I have received a message: " + jsonMessage.body().toString());


            if (jsonMessage.body().containsKey("username")) {
                String username = jsonMessage.body().getString("username");
                if (null != username) {
                    String loginSuccessJson = "{\"login\": \"success\"}";
                    JsonObject loginSuccessful = new JsonObject(loginSuccessJson);
                    getVertx().eventBus().publish("loginresult." + username, loginSuccessful);
                }
            }

        });


//        router.route("/").handler(routingContext -> {
//
//            HttpServerResponse response = routingContext.response();
//            response.putHeader("content-type", "text/html");
//            response.end("Hello World from vertx!!!");
//        });

        SockJSHandlerOptions sockJSHandlerOptionsoptions = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        SockJSHandler sockJsHandler = SockJSHandler.create(getVertx(), sockJSHandlerOptionsoptions);

        BridgeOptions bridgeOptions = new BridgeOptions();
        bridgeOptions.addInboundPermitted(new PermittedOptions().setAddress("login"));
        bridgeOptions.addOutboundPermitted(new PermittedOptions().setAddress("login"));
        bridgeOptions.addOutboundPermitted(new PermittedOptions().setAddressRegex("loginresult\\..+"));
        bridgeOptions.addInboundPermitted(new PermittedOptions().setAddressRegex("loginresult\\..+"));
        sockJsHandler.bridge(bridgeOptions);
        router.route("/eventbus/*").handler(sockJsHandler);

        sockJsHandler.socketHandler(sockJSSocket -> sockJSSocket.handler(sockJSSocket::write));
        router.route("/*").handler(StaticHandler.create());
        server.requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }
}
