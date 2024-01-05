package io.reactorsolutions.vertx_kafka.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class UserVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
  private String username;
  private String deploymentID;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var ctx = vertx.getOrCreateContext();
    deploymentID = ctx.deploymentID();
    this.username = ctx.config().getString("username");
    startPromise.complete();

    vertx.setPeriodic(500, handler -> {
        LOG.debug("{} said: Hello world, this is my id: {}",username, deploymentID);
    });

  }
}
