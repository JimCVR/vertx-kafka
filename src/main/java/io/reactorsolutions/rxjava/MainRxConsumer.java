package io.reactorsolutions.rxjava;

import io.reactorsolutions.rxjava.verticles.RxConsumerVerticle;
import io.vertx.core.Vertx;

public class MainRxConsumer {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(RxConsumerVerticle.class.getName());
  }
}
