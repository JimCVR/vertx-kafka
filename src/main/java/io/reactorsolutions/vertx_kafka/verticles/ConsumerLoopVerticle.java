package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;

public class ConsumerLoopVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(ConsumerLoopVerticle.class);
  private KafkaConsumer<String, JsonObject> consumer;
  private boolean exceptionTrigger;
  private Map options;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    options = new ConsumerOptions().getConfig();
    options.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Duration.ofSeconds(5).toMillis()+"");
    consumer = KafkaConsumer.create(vertx, options);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    consumer.handler(record -> {
      try {
        handler(record);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }).subscribe("vertx").onComplete(
      v -> startPromise.complete(),
      Throwable::printStackTrace);
  }

  private void handler(KafkaConsumerRecord<String, JsonObject> record) throws InterruptedException {
    if (!exceptionTrigger) {
      exceptionTrigger = true;
      try {
        throw new RuntimeException();
      } catch (Exception e) {
        Thread.sleep(Duration.ofSeconds(30).toMillis());
        handler(record);
      }
      LOG.error("Offset {} Key: {} Value: {} ", record.offset(), record.key(), record.value());
    } else {
      System.out.println("Offset: " + record.offset() + " Key: " + record.key() + " Value: " + record.value());
    }
    consumer.commit().onSuccess(handler -> System.out.println("Commited Offset: " + record.offset()));
  }
}
