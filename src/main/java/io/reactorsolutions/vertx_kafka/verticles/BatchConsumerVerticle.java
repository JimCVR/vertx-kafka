package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchConsumerVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(BatchConsumerVerticle.class);
  private KafkaConsumer<String, JsonObject> consumer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    consumer = KafkaConsumer.create(vertx, new ConsumerOptions().getConfig());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    consumer.handler(record->{
      LOG.debug("Handling record which Offset is: {}", record.offset());
    });
    consumer.batchHandler(records -> handlingBatchRecords(records))
      .subscribe("topic33")
      .onComplete(v-> {
            startPromise.complete();
            System.out.println("Subscribed");
          },
        Throwable::printStackTrace);
  }

  private static void handlingBatchRecords(KafkaConsumerRecords<String, JsonObject> records) {
    for (int i = 0; i< records.size(); i++) {
      System.out.println("offset: "+ records.recordAt(i).offset() + " key: "+ records.recordAt(i).key() + " value: " + records.recordAt(i).value());
    }
  }
}
