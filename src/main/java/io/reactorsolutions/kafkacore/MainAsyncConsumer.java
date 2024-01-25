package io.reactorsolutions.kafkacore;

import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static io.reactorsolutions.vertx_kafka.verticles.ConsumerVerticle.SERVER_URI;

public class MainAsyncConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MainAsyncConsumer.class);

  public static void main(String[] args) throws ExecutionException, InterruptedException, URISyntaxException {
    KafkaConsumer<String, JsonObject> consumer = new KafkaConsumer(new ConsumerOptions().getConfig());
    var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
    var client = HttpClient.newHttpClient();

    consumer.subscribe(Arrays.asList("coreConsumer-3"));
    aSyncConsumer(consumer, request, client);
  }

  private static void aSyncConsumer(KafkaConsumer<String, JsonObject> consumer, HttpRequest request, HttpClient client) throws InterruptedException {
    var worker = Executors.newSingleThreadExecutor(r -> new Thread(r, "reactorsolutions-consumer-kafka-thread"));

    while (true) {
      CompletableFuture.supplyAsync(() -> consumer.poll(Duration.ofMillis(1000)), worker).thenAcceptAsync(records -> {
        for (ConsumerRecord<String, JsonObject> record : records) {
          LOG.debug("Record value: {} , offset: {}", record.value(), record.offset());
          client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(result -> System.out.println("body: "+ result.body() +", status code: "+result.statusCode()))
            .thenAccept(v -> LOG.debug("Thread name: {}",Thread.currentThread().getName()))
            .thenAcceptAsync(v -> consumer.commitSync(), worker)
            .thenAccept(v -> LOG.debug("Commited: Thread name: {}",Thread.currentThread().getName()));
        }
      });
      Thread.sleep(1001);
    }
  }
}
