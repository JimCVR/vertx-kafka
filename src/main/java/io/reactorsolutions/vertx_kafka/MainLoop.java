package io.reactorsolutions.vertx_kafka;

import io.vertx.core.Vertx;

public class MainLoop {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(Producer.class.getName());
  }
}
