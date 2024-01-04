package io.reactorsolutions.vertx_kafka;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.Map;

public class Consumer extends AbstractVerticle {
  Map<String, String> config;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    config = new HashMap<>();
    config.put("bootstrap.servers","localhost:29092,localhost:39092");
    config.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    config.put("group.id","my_group");
    config.put("enable.auto.commit","false");

    KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
    consumer.subscribe("topic1").onSuccess(v -> System.out.println("subscribed"));
    consumer.handler( record ->{
      System.out.println("Key : "+ record.key() + ", value: " + record.value());
      consumer.commit();
    });
  }
}
