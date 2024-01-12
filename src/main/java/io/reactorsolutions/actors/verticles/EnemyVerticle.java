package io.reactorsolutions.actors.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnemyVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(EnemyVerticle.class);
  public static final String ENEMY_LOCATION = "damage.dealt";
  public static final String WARRIOR_LOCATION = "damage.received";
  private String deploymentId;
  private final int maxHp = 200;
  private int currentHp;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    currentHp = maxHp;
    deploymentId = vertx.getOrCreateContext().deploymentID();
  }

  @Override
  public void start() {

    handlingDamageReceived();
    vertx.setPeriodic(1000, handler -> attack());
    vertx.setPeriodic(5000, handler -> regen());
  }

  private void attack() {
    var dmg = (int) (Math.random() * 50) + 1;
    vertx.eventBus().publish(ENEMY_LOCATION, dmg);
    LOG.debug("Enemy attacks: {} dmg", dmg);
  }

  private void regen() {
    int regen = (int) ((maxHp * 0.3) + (Math.random() * 20) + 1);
    LOG.debug("Enemy regen: +{}", regen);
    if (maxHp > currentHp + regen) {
      currentHp += regen;
    } else {
      currentHp = maxHp;
    }
    LOG.debug("Enemy hp: {}", currentHp);

  }

  private void handlingDamageReceived() {
    vertx.eventBus().<Integer>consumer(EnemyVerticle.WARRIOR_LOCATION, message -> {
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
}
