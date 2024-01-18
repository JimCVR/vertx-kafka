package io.reactorsolutions.rxjava.verticles;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactorsolutions.vertx_kafka.config.producer.ProducerOptions;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.kafka.client.producer.KafkaProducer;
import io.vertx.rxjava3.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxProducerVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RxProducerVerticle.class);
  private KafkaProducer<String, JsonObject> producer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    producer = KafkaProducer.create(this.vertx, new ProducerOptions().getConfig());
  }

  @Override
  public Completable rxStart() {
    return Flowable.just(1, 2, 3, 4, 5)
      .flatMapSingle(i -> producer.send(KafkaProducerRecord.create("rxJava", "1", new JsonObject().put(i + "", "message " + i))))
      .doOnNext(recordMetadata -> LOG.debug("Offset: {}", recordMetadata.getOffset()))
      .doOnError(Throwable::printStackTrace)
      .ignoreElements();
  }
}
