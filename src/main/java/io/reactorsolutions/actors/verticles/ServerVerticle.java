package io.reactorsolutions.actors.verticles;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactorsolutions.actors.manager.Register;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
  private EventBus eventBus;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    eventBus = vertx.eventBus();
  }

  @Override
  public void start() throws Exception {

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/users").handler(this::getAllUsersHandler);
    router.get("/users/:username").handler(this::getUserHpHandler);
    router.post("/users/:username").handler(this::handleUsers);
    router.post("/users/delete/:username").handler(this::handleDeletedUsers);
    server.requestHandler(router);
    server.listen(8080);
  }

  private void getUserHpHandler(RoutingContext ctx) {
    String usernameParam = ctx.pathParam("username");
    var deploymentId = Register.getConnectedUsers().get(usernameParam);
    HttpServerResponse serverResponse = ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    if (Register.isConnectedUser(usernameParam) && vertx.deploymentIDs().contains(deploymentId)) {
      vertx.eventBus().<JsonObject>request(deploymentId, deploymentId).onSuccess(reply -> {
        LOG.debug("Message received: {}", reply.body().toBuffer());
        serverResponse.setStatusCode(HttpResponseStatus.OK.code()).end(reply.body().toBuffer());
      });
    } else {
      serverResponse.setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end(new JsonObject().toBuffer());
    }
  }

  private void getAllUsersHandler(RoutingContext ctx) {
    JsonObject response = JsonObject.mapFrom(register.getConnectedUsers());
    HttpServerResponse serverResponse = ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    serverResponse.setStatusCode(HttpResponseStatus.OK.code()).end(response.toBuffer());
  }

  private void handleDeletedUsers(RoutingContext ctx) {
    String usernameParam = ctx.pathParam("username");
    String response;
    HttpServerResponse serverResponse = ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
    var deploymentId = Register.getConnectedUsers().get(usernameParam);
    if (Register.isConnectedUser(usernameParam) && vertx.deploymentIDs().contains(deploymentId)) {
      Register.unregister(usernameParam);
      vertx.undeploy(deploymentId);
      response = "Disconnected user with name: " + usernameParam;
      serverResponse.setStatusCode(HttpResponseStatus.OK.code()).end(response);
    } else {
      response = "User with name: " + usernameParam + " doesn't exists";
      serverResponse.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(response);
    }
  }

  private void handleUsers(RoutingContext ctx) {
    String usernameParam = ctx.pathParam("username");
    int hp = (int) (Math.random() * 50) + 100;
    String response;
    HttpServerResponse serverResponse = ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
    if (!Register.isConnectedUser(usernameParam)) {
      vertx.deployVerticle(WarriorVerticle.class.getName(), new DeploymentOptions().setConfig(new JsonObject().put("username", usernameParam).put("hp", hp)))
        .onSuccess(id -> {
            LOG.debug("Deployment successful with id {}", id);
            Register.register(usernameParam, id);
          }
        ).onFailure(err -> LOG.error("Failure! ", err));
      response = "Connected user with name: " + usernameParam;
      serverResponse.setStatusCode(HttpResponseStatus.CREATED.code()).end(response);
    } else {
      response = "User with name: " + usernameParam + " already exists";
      serverResponse.setStatusCode(HttpResponseStatus.PRECONDITION_FAILED.code()).end(response);
    }
  }
}
