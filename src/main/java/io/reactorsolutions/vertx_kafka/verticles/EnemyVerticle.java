package io.reactorsolutions.vertx_kafka.verticles;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnemyVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(EnemyVerticle.class);
  public static final String LOCATION = "damage.done";
  public static final String LOCATION2 = "damage.received";
  private String deploymentId;
  private final int maxHp;
  private int currentHp;

  public EnemyVerticle() {
    maxHp = 200;
    currentHp = maxHp;
  }

  @Override
  public void start() throws Exception {
    deploymentId = vertx.getOrCreateContext().deploymentID();
    consumeMessage();
    vertx.setPeriodic(1000, handler -> attack());
    vertx.setPeriodic(5000, handler -> regen());
  }

  public void attack() {
    var dmg = (int) (Math.random() * 50) + 1;
    vertx.eventBus().publish(LOCATION, dmg);
    LOG.debug("Enemy attacks: {} dmg", dmg);
  }

  public void regen() {
    int regen = (int) ((maxHp * 0.3) + (Math.random() * 20) + 1);
    LOG.debug("Enemy regen: +{}", regen);
    if (maxHp > currentHp + regen) {
      currentHp += regen;
    } else {
      currentHp = maxHp;
    }
    LOG.debug("Enemy hp: {}", currentHp);

  }

  public void consumeMessage() {
    vertx.eventBus().<Integer>consumer(EnemyVerticle.LOCATION2, message -> {
      int dmg = message.body();
      if (currentHp > 0 && currentHp >= dmg) {
        currentHp -= dmg;
        LOG.debug("Enemy : HP {} , damage received {}", currentHp, dmg);
      } else {
        currentHp = 0;
        vertx.undeploy(deploymentId).onSuccess(s -> LOG.debug("Enemy: HP {} , DEFEATED", currentHp));
      }
    });
  }
}
