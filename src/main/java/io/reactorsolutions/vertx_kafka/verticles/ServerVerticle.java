package io.reactorsolutions.vertx_kafka.verticles;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactorsolutions.vertx_kafka.models.Register;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

  Register register;

  public ServerVerticle() {
    register = new Register();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    HttpServer server = vertx.createHttpServer();
    startPromise.complete();
    Router router = Router.router(vertx);
    router.post("/users/:username").handler(ctx -> handleUsers(ctx));
    router.post("/users/delete/:username").handler(ctx -> handleDeletedUsers(ctx));
    server.requestHandler(router);
    server.listen(8080);
  }

  private void handleDeletedUsers(RoutingContext ctx) {
    String usernameParam = ctx.pathParam("username");
    String response;
    HttpServerResponse serverResponse = ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
    if (register.isConnectedUser(usernameParam)) {
      vertx.undeploy(register.getConnectedUsers().get(usernameParam) , handler -> {
        if (handler.succeeded()){
          LOG.debug("Undeploy successful with id {}", register.getConnectedUsers().get(usernameParam));
          register.unregister(usernameParam);
        }
      });
      response = "Disconnected user with name: " + usernameParam;
      serverResponse.setStatusCode(HttpResponseStatus.OK.code()).end(response);
    } else {
      response = "User with name: " + usernameParam + " doesn't exists";
      serverResponse.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(response);
    }

  }

  private void handleUsers(RoutingContext ctx) {
    String usernameParam = ctx.pathParam("username");
    String response;
    HttpServerResponse serverResponse = ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
    if (!register.isConnectedUser(usernameParam)) {
      vertx.deployVerticle(UserVerticle.class.getName(), new DeploymentOptions().setConfig(new JsonObject().put("username", usernameParam)))
        .onSuccess(id -> {
            LOG.debug("Deployment successful with id {}", id);
            register.register(usernameParam,id);
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
