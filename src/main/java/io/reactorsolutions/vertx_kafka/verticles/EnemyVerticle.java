package io.reactorsolutions.vertx_kafka.verticles;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnemyVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(EnemyVerticle.class);
  public static final String LOCATION = "damage.location";

  @Override
  public void start() throws Exception {
    vertx.setPeriodic(1500, handler -> attack());
  }

  public void attack() {
    var dmg = (int)(Math.random()*50)+1;
    vertx.eventBus().publish(LOCATION, dmg);
  }
}
