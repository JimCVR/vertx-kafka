package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ConsumerVerticle.class);
  public static final URI SERVER_URI = URI.create("https://80e3fc1e-acf1-4d12-aaae-04db1f9b9329.mock.pstmn.io/test-ok");
  private KafkaConsumer<String, JsonObject> consumer;
  private WebClient webClient;
  private HttpClient javaClient;
  private String deploymentId;

  private boolean isCommited = false;
  private boolean exceptionTrigger = false;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    consumer = KafkaConsumer.create(vertx, new ConsumerOptions().getConfig());
    webClient = WebClient.create(vertx);
    javaClient = HttpClient.newBuilder().build();
    deploymentId = vertx.getOrCreateContext().deploymentID();
  }

  @Override
  public void start(Promise<Void> startPromise) {
    consumer
      .handler(record -> handler(record))
      .subscribe("topic33").onComplete(
        v -> startPromise.complete(),
        Throwable::printStackTrace);
  }

  private void vertxWebclientHandler(KafkaConsumerRecord<String, JsonObject> record) {
    webClient.getAbs(String.valueOf(SERVER_URI))
      .send()
      .onComplete(result -> {
        System.out.println(Thread.currentThread().getName());
        System.out.println(result.statusCode());
        System.out.println("Offset: " + record.offset());
        System.out.println("Value" + record.value());
        consumer.commit();
      }, Throwable::printStackTrace);
  }

  private void javaWebClientAsyncHandler(KafkaConsumerRecord<String, JsonObject> record) {
    try {
      var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
      javaClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenAccept(result -> System.out.println("body: " + result.body() + ", status code: " + result.statusCode()))
        .thenAccept(v -> System.out.println(Thread.currentThread().getName()))
        .thenCompose(v -> consumer.commit().toCompletionStage());

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(record);
  }

  private void CompletableFutureToVertxFutureHandler() {
    try {
      var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
      Future.fromCompletionStage(javaClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
        .map(result -> {
          System.out.println("body: " + result.body() + ", status code: " + result.statusCode());
          System.out.println(Thread.currentThread().getName());
          return result;
        })
        .flatMap(v -> consumer.commit())
        .onSuccess(v -> System.out.println("We have commited"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handler(KafkaConsumerRecord<String, JsonObject> record) {
    var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
    javaClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenAccept(result -> {
        if (!exceptionTrigger) {
          exceptionTrigger = true;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          LOG.error("Offset {} Key: {} Value: {} ",record.offset(), record.key(), record.value());
          throw new RuntimeException();
        }else{
          System.out.println("Offset: " + record.offset()+ " Key: " +record.key() + " Value: " + record.value());
        }
    }).thenAccept(v -> {
      consumer.commit().onSuccess(handler -> System.out.println("Commited Offset: "+ record.offset()));
    });
  }

  private void singleCommitHandler() {
    if (!isCommited) {
      isCommited = true;
      consumer.commit().onSuccess(s -> {
          System.out.println("commited");
        }
      );
    }
  }
}
