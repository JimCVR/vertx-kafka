package io.reactorsolutions.rxjava.verticles;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.rxjava3.core.AbstractVerticle;

public class RxConsumerVerticle extends AbstractVerticle {

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    startFuture.complete();
  }
}
