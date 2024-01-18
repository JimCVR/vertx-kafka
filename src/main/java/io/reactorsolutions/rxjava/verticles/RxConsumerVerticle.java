package io.reactorsolutions.rxjava.verticles;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.kafka.client.consumer.KafkaConsumer;
import io.vertx.rxjava3.kafka.client.consumer.KafkaConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class RxConsumerVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RxConsumerVerticle.class);

  private KafkaConsumer<String, JsonObject> consumer;


  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    consumer = KafkaConsumer.create(this.vertx, new ConsumerOptions().getConfig());
  }

  @Override
  public Completable rxStart() {
    return consumer.subscribe("rxJava").andThen((CompletableSource) completableObserver ->
      consumer
        .toFlowable()
        .map(record -> record.value())
        .buffer(5)
        .map(ArrayList::new)
        .subscribe(val -> val.forEach(it-> System.out.println(it.toString()))));
  }

  private void consumerHandler(KafkaConsumerRecord<String, JsonObject> record) {
    LOG.debug("Offset: {} , Key: {} , Value {}", record.offset(), record.key(), record.value());
    consumer.commit();
  }
}
