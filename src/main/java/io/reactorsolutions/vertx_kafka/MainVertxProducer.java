package io.reactorsolutions.vertx_kafka;

import io.reactorsolutions.vertx_kafka.verticles.ProducerVerticle;
import io.vertx.core.Vertx;

public class MainVertxProducer {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(ProducerVerticle.class.getName());
  }
}
