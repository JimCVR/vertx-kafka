package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.models.Fighter;
import io.reactorsolutions.vertx_kafka.producer_consumer.Consumer;
import io.reactorsolutions.vertx_kafka.producer_consumer.Producer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrolyVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(BrolyVerticle.class);
  //Se envian y reciben JSON de broly
  public static final String FIRSTLOCATION = "planet.vegeta";
  //Se envian y reciben String de daño del cliente
  public static final String SECONDLOCATION = "planet.earth";

  public Producer producer;
  public Consumer consumer;
  private Fighter fighter;

  public BrolyVerticle() {
    producer = new Producer(vertx);
    consumer = new Consumer(vertx);
    fighter = new Fighter("Broly", 500, 500, 20, 50, 500, 100, 100, "KAKAROTTOOO!");
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String deploymentID = vertx.getOrCreateContext().deploymentID();
    startPromise.complete();
    producer.getProducer().send(KafkaProducerRecord.create("broly", "1", fighter.toJsonObject())).onSuccess(recordMetadata -> {
      System.out.println(" written on topic=" + recordMetadata.getTopic() +
        ", partition=" + recordMetadata.getPartition() + ", offset=" + recordMetadata.getOffset()
      );
    });
    //vertx.eventBus().consumer(SECONDLOCATION, getDamageHandler(deploymentID));
    vertx.setPeriodic(fighter.aSpd() + 3000, handler -> fighterTurn());
  }

  public Handler<Message<String>> getDamageHandler(String deploymentID) {
    return message -> {
      try {
        var dmg = Integer.parseInt(message.body());
        LOG.debug("Client attacks!: {} hit points", dmg);
        fighter.takeDmg(dmg);
        vertx.eventBus().publish(FIRSTLOCATION, fighter.toJsonObject());
        fighter.defeatedFighter(vertx, deploymentID);
        fighter.showStatus();
      } catch (NumberFormatException e) {
        LOG.error("Number Format Exception", e);
      }
    };
  }

  private void heartBeat() {
    vertx.setPeriodic(100, handler -> vertx.eventBus().publish(FIRSTLOCATION, fighter.toJsonObject()));
    vertx.eventBus().publish(FIRSTLOCATION, fighter.toJsonObject());
  }

  public void fighterTurn() {
    fighter.attack();
    fighter.checkStats();
    producer.getProducer().send(KafkaProducerRecord.create("broly", "1", fighter.toJsonObject())).onSuccess(recordMetadata -> {
      System.out.println(" written on topic=" + recordMetadata.getTopic() +
        ", partition=" + recordMetadata.getPartition() + ", offset=" + recordMetadata.getOffset()
      );
    });
  }
}
