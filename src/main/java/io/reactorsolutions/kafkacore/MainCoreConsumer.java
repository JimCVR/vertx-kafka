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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static io.reactorsolutions.vertx_kafka.verticles.ConsumerVerticle.SERVER_URI;

public class MainCoreConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MainCoreConsumer.class);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    KafkaConsumer<String, JsonObject> consumer = new KafkaConsumer(new ConsumerOptions().getConfig());
    var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
    var client  = HttpClient.newHttpClient();

    consumer.subscribe(Arrays.asList("coreConsumer-3"));
    var worker = Executors.newSingleThreadExecutor(r -> new Thread(r, "reactorsolutions-consumer-kafka-thread"));
    while (true) {
      CompletableFuture.supplyAsync(()-> consumer.poll(Duration.ofMillis(100)), worker).thenApply(records -> {
        for (ConsumerRecord<String, JsonObject> record : records) {
          LOG.debug("Record value: {} , offset: {}",record.value(), record.offset());
          client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
      })

      /*for (ConsumerRecord<String, JsonObject> record : records) {
        LOG.debug("Record value: {} , offset: {}",record.value(), record.offset());
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenAccept(result -> System.out.println("body: "+ result.body() +", status code: "+result.statusCode()))
          .thenAccept(v -> System.out.println(Thread.currentThread().getName()))
          .thenAccept(v -> {
            LOG.debug("Commiting message");
            try{
              consumer.commitSync();

            }catch (Exception e){
              e.printStackTrace();
            }
          })
          .thenAccept(v -> System.out.println("Committed"));
      }
    }
  }
}
