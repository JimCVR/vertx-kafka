package io.reactorsolutions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("INFO log from {}", Main.class.getSimpleName());
    logger.warn("WARN log from {}", Main.class.getSimpleName());
    logger.debug("DEBUG log from {}", Main.class.getSimpleName());
    logger.error("ERROR log from {}", Main.class.getSimpleName());
    logger.trace("TRACE log from {}", Main.class.getSimpleName());
  }
}
