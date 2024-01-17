package io.reactorsolutions.vertx_kafka.config.producer;

import java.util.HashMap;
import java.util.Map;

public class ProducerOptions {
  private Map<String, String> config;

  public ProducerOptions() {
    config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:29092,localhost:39092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    config.put("acks", "1");
  }

  public Map<String, String> getConfig() {
    return config;
  }
}
