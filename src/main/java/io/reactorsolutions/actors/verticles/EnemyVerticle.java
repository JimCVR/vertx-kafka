package io.reactorsolutions.actors.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnemyVerticle extends AbstractVerticle {

  public static final String ENEMY_LOCATION = "damage.dealt";
  public static final String WARRIOR_LOCATION = "damage.received";
  private static final Logger LOG = LoggerFactory.getLogger(EnemyVerticle.class);
  private static final int MAX_HP = 200;
  private String deploymentId;
  private EventBus eventBus;
  private int currentHp;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    eventBus = vertx.eventBus();
    currentHp = MAX_HP;
    deploymentId = vertx.getOrCreateContext().deploymentID();
  }

  @Override
  public void start() {

    handlingDamageReceived();
    vertx.setPeriodic(1000, handler -> attack());
    vertx.setPeriodic(5000, handler -> regen());
  }

  private void handlingDamageReceived() {
    eventBus.<Integer>consumer(EnemyVerticle.WARRIOR_LOCATION, message -> {
      int dmg = message.body();
      if (currentHp > 0 && currentHp >= dmg) {
        currentHp -= dmg;
        LOG.debug("Enemy : HP {} , damage received {}", currentHp, dmg);
      } else {
        currentHp = 0;
        vertx.undeploy(deploymentId)
          .onSuccess(s -> LOG.debug("Enemy: HP {} , DEFEATED", currentHp));
      }
    });
  }

  private void attack() {
    var dmg = (int) (Math.random() * 50) + 1;
    eventBus.publish(ENEMY_LOCATION, dmg);
    LOG.debug("Enemy attacks: {} dmg", dmg);
  }

  private void regen() {
    int regen = (int) ((MAX_HP * 0.3) + (Math.random() * 20) + 1);
    LOG.debug("Enemy regen: +{}", regen);
    if (MAX_HP > currentHp + regen) {
      currentHp += regen;
    } else {
      currentHp = MAX_HP;
    }
    LOG.debug("Enemy hp: {}", currentHp);
  }
}
