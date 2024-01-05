package io.reactorsolutions.vertx_kafka.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.Map;

public class ConsumerExample extends AbstractVerticle {
  Map<String, String> config;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    config = new HashMap<>();
    config.put("bootstrap.servers","localhost:29092,localhost:39092");
    //config.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    //config.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
    config.put("group.id","my_group");
    config.put("enable.auto.commit","false");
    startPromise.complete();
    KafkaConsumer<String, JsonObject> consumer = KafkaConsumer.create(vertx, config, String.class, JsonObject.class);
    consumer.subscribe("topic1").onSuccess(v -> System.out.println("subscribed")).onFailure(err-> err.printStackTrace());
    consumer.handler( record ->{
      System.out.println("Key : "+ record.key() + ", value: " + record.value());
      consumer.commit();
    });
  }
}
