package io.reactorsolutions.vertx_kafka;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Producer extends AbstractVerticle {
  private int counter;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:29092,localhost:39092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("acks", "1");

    KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);

    vertx.setPeriodic(200, handler -> {
      producer.send(KafkaProducerRecord.create("topic1", "hello world " +counter))
        .onSuccess(recordMetadata ->{
          counter++;

          System.out.println(" written on topic=" + recordMetadata.getTopic() +
            ", partition=" + recordMetadata.getPartition() + ", offset=" + recordMetadata.getOffset()
          );
        })
        .onFailure(Throwable::printStackTrace);
    });
    startPromise.complete();
  }
}
