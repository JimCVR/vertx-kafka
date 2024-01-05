package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.handlers.WebSocketHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer(new HttpServerOptions().setRegisterWebSocketWriteHandlers(true))
      .webSocketHandler(new WebSocketHandler(vertx))
      .listen(8900, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8900");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
