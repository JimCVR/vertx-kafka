package io.reactorsolutions.vertx_kafka.verticles;

import io.reactorsolutions.vertx_kafka.models.Fighter;
import io.reactorsolutions.vertx_kafka.config.consumer.ConsumerOptions;
import io.reactorsolutions.vertx_kafka.config.producer.ProducerOptions;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrolyVerticle extends AbstractVerticle {

  //Send and receives broly's JsonObject
  public static final String FIRSTLOCATION = "planet.vegeta";
  //Send and receives client's damage String
  public static final String SECONDLOCATION = "planet.earth";
  private static final Logger LOG = LoggerFactory.getLogger(BrolyVerticle.class);

  private KafkaProducer<String, JsonObject> producer;
  private KafkaConsumer<String, JsonObject> consumer;
  private Fighter fighter;
  private String deploymentID;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    fighter = new Fighter("Broly", 500, 500, 20, 50, 500, 100, 100, "KAKAROTTOOO!");
    producer = KafkaProducer.create(vertx,new ProducerOptions().getConfig());
    consumer = KafkaConsumer.create(vertx,new ConsumerOptions().getConfig());
    deploymentID = vertx.getOrCreateContext().deploymentID();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    producer.send(KafkaProducerRecord.create("broly", "1", fighter.toJsonObject())).onSuccess(v ->{
      LOG.debug("Message sent");
    });
    vertx.setPeriodic(fighter.aSpd()+2000, handler -> fighterTurn());
    startPromise.complete();
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
    producer.send(KafkaProducerRecord.create("broly", "1", fighter.toJsonObject()))
      .onSuccess(v -> LOG.debug("Message sent")
      ).onFailure(Throwable::printStackTrace);
  }
}
