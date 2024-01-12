package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.producer_consumer.Consumer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ConsumerExample extends AbstractVerticle {

  Consumer consumer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    consumer = new Consumer(vertx);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    consumer.getJsonConsumer().subscribe("topic1")
      .onSuccess(v -> System.out.println("subscribed"))
      .onFailure(err -> err.printStackTrace());
    consumer.getJsonConsumer().handler(record -> {
      try {
        HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI("http://localhost:8080/users/"))
          .POST(HttpRequest.BodyPublishers.ofString(record.key()))
          .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      } catch (Exception e) {
        e.printStackTrace();
      }
      consumer.getJsonConsumer().commit();
    });
    startPromise.complete();
  }
}
