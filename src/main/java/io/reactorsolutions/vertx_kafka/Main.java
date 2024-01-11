package io.reactorsolutions.vertx_kafka;

import io.vertx.core.Vertx;

public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    //vertx.deployVerticle(ConsumerExample.class.getName());
    vertx.deployVerticle(ServerVerticle.class.getName());
  }
}
