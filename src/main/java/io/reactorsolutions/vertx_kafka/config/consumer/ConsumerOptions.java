package io.reactorsolutions.vertx_kafka.config.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsumerOptions {
  Map<String, String> config;

  public ConsumerOptions() {
    config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:29092,localhost:39092");
    config.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
    config.put("auto.offset.reset", "earliest");
    config.put("group.id", "reactorSolutions-2");
    config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG,("SchedulerCoordinator"+ UUID.randomUUID()));
    config.put("enable.auto.commit", "false");
  }

  public Map<String, String> getConfig() {
    return config;
  }
}
