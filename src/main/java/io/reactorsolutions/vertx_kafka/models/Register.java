package io.reactorsolutions.vertx_kafka.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Register {

  private static final Logger LOG = LoggerFactory.getLogger(Register.class);
  private final Map<String, String> connectedUsers = new HashMap<>();

  public void register(String username, String deploymentId){
    connectedUsers.put(username, deploymentId);
    LOG.debug("Registered user with id {}", deploymentId);
  }

  public void unregister(String username){
    connectedUsers.remove(username);
    LOG.debug("Removed user with id {}", username);
  }

  public Map<String, String> getConnectedUsers() {
    return connectedUsers;
  }

  public boolean isConnectedUser(String username){
    return connectedUsers.containsKey(username);
  }

}
