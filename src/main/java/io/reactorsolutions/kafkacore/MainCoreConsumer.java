package io.reactorsolutions.kafkacore;

import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static io.reactorsolutions.vertx_kafka.verticles.ConsumerVerticle.SERVER_URI;

public class MainCoreConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MainCoreConsumer.class);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    KafkaConsumer<String, JsonObject> consumer = new KafkaConsumer<>(new ConsumerOptions().getConfig());
    var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
    var client  = HttpClient.newHttpClient();

    consumer.subscribe(Arrays.asList("baeldung"));

    while (true) {
      ConsumerRecords<String, JsonObject> records = consumer.poll(Duration.ofMillis(100));
      System.out.println("polling");

      for (ConsumerRecord<String, JsonObject> record : records) {
        LOG.debug("Record value: {} , offset: {}",record.value(), record.offset());
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenAccept(result -> System.out.println("body: "+ result.body() +", status code: "+result.statusCode()))
          .thenAccept(v -> System.out.println(Thread.currentThread().getName()))
          .thenAccept(v -> consumer.commitAsync()).get();
      }
    }
  }
}
