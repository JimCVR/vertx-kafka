package io.reactorsolutions.vertx_kafka.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class UserVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
  private String username;
  private String deploymentID;

  private int hp;

  @Override
  public void start() throws Exception {
    var ctx = vertx.getOrCreateContext();
    deploymentID = ctx.deploymentID();
    this.username = ctx.config().getString("username");
    this.hp = ctx.config().getInteger("hp");
    sendStatus();
    LOG.debug("{} said: Hello world, this is my id: {} and my hp {}", username, deploymentID, hp);
  }

  public void sendStatus() {
    JsonObject userData = new JsonObject()
      .put("username", username)
      .put("deploymentID", deploymentID)
      .put("hp", hp);
    vertx.eventBus().consumer(deploymentID, message -> {
      message.reply(userData);
      LOG.debug("Reply sent: {}", userData.toString());
    });
  }

  public void consumeMessage() {
    vertx.eventBus().<Integer>consumer(deploymentID + ".consumer", message -> {
      int dmg = message.body();
      hp -= dmg;
    });
  }
}
