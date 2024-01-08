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
    sendStatus();consumeMessage();
    LOG.debug("{} said: Hello world, this is my id: {} and my hp {}", username, deploymentID, hp);
  }

  public void sendStatus() {
    vertx.eventBus().consumer(deploymentID, message -> {
      JsonObject userData = new JsonObject()
        .put("username", username)
        .put("deploymentID", deploymentID)
        .put("hp", hp);
      message.reply(userData);
      LOG.debug("Reply sent: {}", userData.toString());
    });
  }

  public void consumeMessage() {
    vertx.eventBus().<Integer>consumer(EnemyVerticle.LOCATION, message -> {
      int dmg = message.body();
      if(hp > 0 && hp >= dmg){
        hp -= dmg;
        LOG.debug("{} : current hp {} , damage received {}", username, hp , dmg);
      } else {
        hp = 0;
        vertx.undeploy(deploymentID).onSuccess(s -> LOG.debug("{}: current hp {} , defeated", username, hp));
      }
    });
  }
}
