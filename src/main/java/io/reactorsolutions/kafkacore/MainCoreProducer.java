package io.reactorsolutions.kafkacore;

import io.reactorsolutions.vertx_kafka.config.producer.ProducerOptions;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class MainCoreProducer {
  private static final Logger LOG = LoggerFactory.getLogger(MainCoreProducer.class);
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    KafkaProducer<String, JsonObject> producer = new KafkaProducer(new ProducerOptions().getConfig());

    for (int i = 1; i <= 2; i++) {
      ProducerRecord<String, JsonObject> record = new ProducerRecord<>("coreConsumer-3", new JsonObject().put("key "+i ,String.valueOf(i)));
      producer.send(record).get();
      LOG.debug("sending {}", record.value());
    }
  }
}
