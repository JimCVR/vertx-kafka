package io.reactorsolutions.vertx_kafka.broadcast;

import io.reactorsolutions.vertx_kafka.verticles.BrolyVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FightBroadcast {

  private static final Logger LOG = LoggerFactory.getLogger(FightBroadcast.class);
  private final Map<String, ServerWebSocket> connectedClients = new HashMap<>();
  private JsonObject brolyjson;


  public FightBroadcast(final Vertx vertx) {
    periodicUpdate(vertx);
  }

  private void periodicUpdate(final Vertx vertx) {
    vertx.eventBus().<JsonObject>consumer(BrolyVerticle.FIRSTLOCATION, message -> {
      brolyjson = message.body();
      LOG.debug("Message: {}", brolyjson.toString());
        connectedClients.values().forEach(ws -> {
          ws.writeTextMessage(brolyjson.toString());
        });
    });
  }


  public void register(ServerWebSocket ws) {
    connectedClients.put(ws.textHandlerID(), ws);
    ws.writeTextMessage(brolyjson.toString());
  }

  public void unregister(ServerWebSocket ws) {
    connectedClients.remove(ws.textHandlerID());
  }

}
