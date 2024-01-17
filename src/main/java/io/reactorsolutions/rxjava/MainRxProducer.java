package io.reactorsolutions.rxjava;

import io.reactorsolutions.rxjava.verticles.RxProducerVerticle;
import io.vertx.core.Vertx;

public class MainRxProducer {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(RxProducerVerticle.class.getName());
  }
}
