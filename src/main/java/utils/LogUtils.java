package utils;

import app.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtils {

  static Logger debugLogger = null;
  public static Logger getLogger() {
    var debugLogger = LogManager.getLogger(Main.class);
    LogUtils.debugLogger = debugLogger;
    return debugLogger;
  }

  public static void printDebugLog(String message) {
    if (!("prod").equals(System.getenv("ENV"))) {
      if (debugLogger != null) {
        debugLogger.info(message);
      }
    }
  }

}
