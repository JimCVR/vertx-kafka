package io.reactorsolutions.vertx_kafka;

import io.reactorsolutions.Main;
import io.reactorsolutions.vertx_kafka.verticles.ConsumerVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVertxConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MainVertxConsumer.class);
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(ConsumerVerticle.class.getName()).onSuccess(v -> System.out.println("Consumer deployed"));
    //vertx.deployVerticle(BatchConsumerVerticle.class.getName()).onSuccess(v -> System.out.println("Consumer deployed"));
  }
}
