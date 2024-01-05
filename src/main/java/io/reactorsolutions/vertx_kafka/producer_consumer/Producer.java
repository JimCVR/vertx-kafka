package io.reactorsolutions.vertx_kafka.producer_consumer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

public class Producer {
  private Map<String, String> config;

  private KafkaProducer<String, JsonObject> producer;

  public Producer(Vertx vertx) {
    config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:29092,localhost:39092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    config.put("acks", "1");
    producer = KafkaProducer.create(vertx, config);
  }

  public KafkaProducer<String, JsonObject> getProducer() {
    return producer;
  }
}
