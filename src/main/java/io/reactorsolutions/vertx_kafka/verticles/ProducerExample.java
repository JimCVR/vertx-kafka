package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.producer_consumer.Producer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import netscape.javascript.JSObject;

import java.util.HashMap;
import java.util.Map;

public class ProducerExample extends AbstractVerticle {
  private int counter;
  private KafkaProducer<String, JSObject> producer;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:29092,localhost:39092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    config.put("acks", "1");

    Producer producer = new Producer(vertx);

    vertx.setPeriodic(200, handler -> {
      producer.getProducer().send(KafkaProducerRecord.create("topic1","key1" ,new JsonObject().put(counter+"","hello world " +counter)))
        .onSuccess(recordMetadata -> counter++)
        .onFailure(Throwable::printStackTrace);
    });
    startPromise.complete();
  }
}
