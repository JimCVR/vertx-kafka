package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.config.producer.ProducerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

public class ProducerVerticle extends AbstractVerticle {
  private static final String KEY_1 = "kafka";
  private static final String KEY_2 = "vertx";
  private KafkaProducer<String, JsonObject> producer;


  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    producer = KafkaProducer.create(vertx, new ProducerOptions().getConfig());
  }

  @Override
  public void start(Promise<Void> startPromise) {
    for (int i = 0; i < 3; i++) {
      producer.send(KafkaProducerRecord.create(KEY_2, new JsonObject().put(KEY_1 + "", "" + i)))
        .onSuccess(recordMetadata -> System.out.println(recordMetadata.getOffset()))
        .onFailure(Throwable::printStackTrace);
    }
    startPromise.complete();
  }
}
