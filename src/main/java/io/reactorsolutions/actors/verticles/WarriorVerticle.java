package io.reactorsolutions.actors.verticles;

import io.reactorsolutions.actors.manager.Register;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarriorVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
  private String username;
  private String deploymentID;
  private EventBus eventBus;
  private int maxHp;
  private int currentHp;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    var ctx = vertx.getOrCreateContext();
    var config = ctx.config();
    deploymentID = ctx.deploymentID();
    username = config.getString("username");
    maxHp = config.getInteger("hp");
    currentHp = maxHp;
    eventBus = vertx.eventBus();
  }

  @Override
  public void start() {
    handlingStatusRequest();
    handlingDamageReceived();
    vertx.setPeriodic(1000, handler -> attack());

    LOG.debug("{} JOINS THE BATTLE!", username);
  }

  private void handlingStatusRequest() {
    eventBus.consumer(deploymentID, message -> {
      JsonObject userData = new JsonObject()
        .put("username", username)
        .put("deploymentID", deploymentID)
        .put("hp", currentHp);
      message.reply(userData);
      LOG.debug("Reply sent: {}", userData.toString());
    });
  }

  private void handlingDamageReceived() {
    eventBus.<Integer>consumer(EnemyVerticle.ENEMY_LOCATION, message -> {
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

  private void attack() {
    var dmg = (int) (Math.random() * 50) + 1;
    LOG.debug("{} attacks: {} dmg", username, dmg);
    eventBus.send(EnemyVerticle.WARRIOR_LOCATION, dmg);
  }
}
