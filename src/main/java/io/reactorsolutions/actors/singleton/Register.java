package io.reactorsolutions.actors.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class Register {

  private static final Logger LOG = LoggerFactory.getLogger(Register.class);

  private final static Map<String, String> connectedUsers;

  private Register() {};

  static {
    connectedUsers = new HashMap<>();
  }

  public static void register(String username, String deploymentId) {
    connectedUsers.put(username, deploymentId);
    LOG.debug("Registered user with id {}", deploymentId);
  }

  public static void unregister(String username) {
    connectedUsers.remove(username);
    LOG.debug("Removed user with id {}", username);
  }

  public static Map<String, String> getConnectedUsers() {
    return connectedUsers;
  }

  public static boolean isConnectedUser(String username) {
    return connectedUsers.containsKey(username);
  }
}
