package io.reactorsolutions.vertx_kafka;

import io.reactorsolutions.vertx_kafka.verticles.ProducerExample;
import io.vertx.core.Vertx;

public class MainProducer {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(ProducerExample.class.getName());
  }
}
