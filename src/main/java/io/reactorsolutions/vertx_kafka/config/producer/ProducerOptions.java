package io.reactorsolutions.vertx_kafka.config.producer;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

public class ProducerOptions {
  private Map<String, String> config;

  public ProducerOptions() {
    config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    config.put(ProducerConfig.ACKS_CONFIG, "1");
  }

  public Map<String, String> getConfig() {
    return config;
  }
}
