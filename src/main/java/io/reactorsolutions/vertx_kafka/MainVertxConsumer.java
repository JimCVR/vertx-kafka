package io.reactorsolutions.vertx_kafka;

import io.reactorsolutions.vertx_kafka.verticles.ConsumerExample;
import io.vertx.core.Vertx;

public class MainVertxConsumer {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(ConsumerExample.class.getName()).onSuccess(v -> System.out.println("Consumer deployed"));
    //vertx.deployVerticle(BatchConsumerVerticle.class.getName()).onSuccess(v -> System.out.println("Consumer deployed"));
  }
}
