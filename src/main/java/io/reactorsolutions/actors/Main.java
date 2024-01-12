package io.reactorsolutions.actors;

import io.reactorsolutions.actors.verticles.EnemyVerticle;
import io.reactorsolutions.actors.verticles.ServerVerticle;
import io.vertx.core.Vertx;

public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(EnemyVerticle.class.getName()).onComplete(
      success-> vertx.deployVerticle(ServerVerticle.class.getName()),
      Throwable::printStackTrace
    );
  }
}
