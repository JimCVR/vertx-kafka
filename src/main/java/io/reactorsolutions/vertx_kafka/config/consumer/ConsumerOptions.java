package io.reactorsolutions.vertx_kafka.config.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsumerOptions {
  Map<String, String> config;

  public ConsumerOptions() {
    config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "reactorSolutions-2");
    config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG,("SchedulerCoordinator"+ UUID.randomUUID()));
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
  }

  public Map<String, String> getConfig() {
    return config;
  }
}
