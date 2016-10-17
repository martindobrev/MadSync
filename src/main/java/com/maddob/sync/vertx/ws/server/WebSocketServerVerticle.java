package com.maddob.sync.vertx.ws.server;

import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;

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

//        router.route("/").handler(routingContext -> {
//
//            HttpServerResponse response = routingContext.response();
//            response.putHeader("content-type", "text/html");
//            response.end("Hello World from vertx!!!");
//        });

        SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        SockJSHandler sockJsHandler = SockJSHandler.create(getVertx(), options);
        sockJsHandler.socketHandler(sockJSSocket -> sockJSSocket.handler(sockJSSocket::write));

        router.route("/myapp/*").handler(sockJsHandler);
        router.route("/*").handler(StaticHandler.create());
        server.requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }
}
