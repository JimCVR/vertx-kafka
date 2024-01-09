package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.models.Register;
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

  private int maxHp;
  private int currentHp;

  private Register register;

  @Override
  public void start() throws Exception {
    var ctx = vertx.getOrCreateContext();
    deploymentID = ctx.deploymentID();
    username = ctx.config().getString("username");
    maxHp = ctx.config().getInteger("hp");
    currentHp = maxHp;
    register = Register.getInstance();
    sendStatus();
    consumeMessage();
    vertx.setPeriodic(1000, handler -> attack());
    LOG.debug("{} JOINS THE BATTLE!", username);
  }

  public void attack() {
    var dmg = (int) (Math.random() * 50) + 1;
    LOG.debug("{} attacks: {} dmg", username, dmg);
    vertx.eventBus().send(EnemyVerticle.LOCATION2, dmg);
  }

  public void sendStatus() {
    vertx.eventBus().consumer(deploymentID, message -> {
      JsonObject userData = new JsonObject()
        .put("username", username)
        .put("deploymentID", deploymentID)
        .put("hp", currentHp);
      message.reply(userData);
      LOG.debug("Reply sent: {}", userData.toString());
    });
  }

  public void consumeMessage() {
    vertx.eventBus().<Integer>consumer(EnemyVerticle.LOCATION, message -> {
      int dmg = message.body();
      if (currentHp > 0 && currentHp >= dmg) {
        currentHp -= dmg;
        LOG.debug("{} : hp {}/{} , damage received {}", username, currentHp, maxHp, dmg);
      } else {
        currentHp = 0;
        register.unregister(username);
        vertx.undeploy(deploymentID).onSuccess(s -> LOG.debug("{}: current hp {} , defeated", username, currentHp));
      }
    });
  }
}
