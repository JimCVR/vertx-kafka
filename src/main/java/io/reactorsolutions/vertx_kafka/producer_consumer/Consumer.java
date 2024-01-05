package io.reactorsolutions.vertx_kafka.producer_consumer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.Map;

public class Consumer {
  Map<String, String> config;
  KafkaConsumer<String, JsonObject> jsonConsumer;

  public Consumer(Vertx vertx) {
    config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:29092,localhost:39092");
    config.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
    config.put("group.id", "my_group");
    config.put("enable.auto.commit", "false");
    jsonConsumer = KafkaConsumer.create(vertx, config);
  }

  public KafkaConsumer<String, JsonObject> getJsonConsumer() {
    return jsonConsumer;
  }
}
