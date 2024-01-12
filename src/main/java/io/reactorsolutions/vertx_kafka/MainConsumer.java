package io.reactorsolutions.vertx_kafka;

import io.reactorsolutions.vertx_kafka.verticles.BrolyVerticle;
import io.reactorsolutions.vertx_kafka.verticles.ConsumerExample;
import io.vertx.core.Vertx;

public class MainConsumer {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(ConsumerExample.class.getName());
  }
}
