package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerLoopVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ConsumerLoopVerticle.class);
  private KafkaConsumer<String, JsonObject> consumer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    consumer = KafkaConsumer.create(vertx, new ConsumerOptions().getConfig());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
  }
}
