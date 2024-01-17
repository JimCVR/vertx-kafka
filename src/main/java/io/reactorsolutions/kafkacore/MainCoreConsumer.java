package io.reactorsolutions.kafkacore;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static io.reactorsolutions.vertx_kafka.verticles.ConsumerVerticle.SERVER_URI;

public class MainCoreConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MainCoreConsumer.class);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Properties consumerProperties = new Properties();
    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092");
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "manolito");
    consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProperties.put("enable.auto.commit", "false");
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);

    consumer.subscribe(Arrays.asList("baeldung"));
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      System.out.println("polling");

      for (ConsumerRecord<String, String> record : records) {
        LOG.debug("Record value: {} , offset: {}",record.value(), record.offset());
        //consumer.commitSync();
        //CompletableFuture.runAsync(consumer::commitSync).get();
        var request = HttpRequest.newBuilder().uri(SERVER_URI).build();
        var client  = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenAccept(result -> System.out.println("body: "+ result.body() +", status code: "+result.statusCode()))
          .thenAccept(v -> System.out.println(Thread.currentThread().getName()))
          .thenAccept(v -> consumer.commitAsync()).get();
      }
    }
  }
}
