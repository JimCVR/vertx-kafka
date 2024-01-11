package io.reactorsolutions.actors.verticles;

import io.reactorsolutions.actors.singleton.Register;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarriorVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
  private String username;
  private String deploymentID;

  private int maxHp;
  private int currentHp;

  @Override
  public void start() {
    var ctx = vertx.getOrCreateContext();
    deploymentID = ctx.deploymentID();
    username = ctx.config().getString("username");
    maxHp = ctx.config().getInteger("hp");
    currentHp = maxHp;

    handlingStatusRequest();
    handlingDamageReceived();
    vertx.setPeriodic(1000, handler -> attack());

    LOG.debug("{} JOINS THE BATTLE!", username);
  }

  private void attack() {
    var dmg = (int) (Math.random() * 50) + 1;
    LOG.debug("{} attacks: {} dmg", username, dmg);
    vertx.eventBus().send(EnemyVerticle.WARRIOR_LOCATION, dmg);
  }

  private void handlingStatusRequest() {
    vertx.eventBus().consumer(deploymentID, message -> {
      JsonObject userData = new JsonObject()
        .put("username", username)
        .put("deploymentID", deploymentID)
        .put("hp", currentHp);
      message.reply(userData);
      LOG.debug("Reply sent: {}", userData.toString());
    });
  }

  private void handlingDamageReceived() {
    vertx.eventBus().<Integer>consumer(EnemyVerticle.ENEMY_LOCATION, message -> {
      int dmg = message.body();
      if (currentHp > 0 && currentHp >= dmg) {
        currentHp -= dmg;
        LOG.debug("{} : hp {}/{} , damage received {}", username, currentHp, maxHp, dmg);
      } else {
        currentHp = 0;
        Register.unregister(username);
        vertx.undeploy(deploymentID).onSuccess(s -> LOG.debug("{}: current hp {} , defeated", username, currentHp));
      }
    });
  }
}
