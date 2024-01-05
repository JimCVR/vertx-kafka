package io.reactorsolutions.vertx_kafka.handlers;

import io.reactorsolutions.vertx_kafka.broadcast.FightBroadcast;
import io.reactorsolutions.vertx_kafka.verticles.BrolyVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler implements Handler<ServerWebSocket> {

  private final static Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);
  public static final String PATH = "/ws/broly";
  private final EventBus eventBus;

  private final FightBroadcast fightBroadcast;

  public WebSocketHandler(final Vertx vertx) {
    eventBus = vertx.eventBus();
    fightBroadcast = new FightBroadcast(vertx);
  }

  @Override
  public void handle(ServerWebSocket ws) {
    if (!PATH.equalsIgnoreCase(ws.path())) {
      LOG.info("Rejected wrong path: {}", ws.path());
      ws.writeFinalTextFrame("Wrong path. Only " + PATH + " is accepted!");
      closeClient(ws);
      return;
    }

    ws.accept();
    LOG.info("Connected to url {} with client id {}", ws.path(), ws.textHandlerID());
   // ws.writeTextMessage(brolyjson.toString());
    ws.frameHandler(getFrameHandler(ws));
    ws.endHandler(close -> {
      LOG.info("connection terminated: {}", ws.textHandlerID());
      fightBroadcast.unregister(ws);
    });
    ws.exceptionHandler(err -> LOG.error("Failed: ", err));
    fightBroadcast.register(ws);
  }

  private Handler<WebSocketFrame> getFrameHandler(ServerWebSocket ws) {
    return received -> {
      final String message = received.textData();
      eventBus.send(BrolyVerticle.SECONDLOCATION, message);

      if ("disconnect me".equalsIgnoreCase((message))) {
        LOG.info("Client close requested!");
        closeClient(ws);
      } else {
        ws.writeTextMessage("Not supported -> (" + message + ")");
      }
    };
  }

  private static void closeClient(ServerWebSocket ws) {
    ws.close((short) 1000, "Normal Closure");
  }
}
