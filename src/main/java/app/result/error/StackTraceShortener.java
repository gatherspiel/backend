package app.result.error;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StackTraceShortener {
  public static Set<String> packagesToIgnore;
  static {
    packagesToIgnore = new HashSet<String>();

    packagesToIgnore.add("com.fasterxml.jackson.core");
    packagesToIgnore.add("com.fasterxml.jackson.databind");

    packagesToIgnore.add("io.javalin");

    packagesToIgnore.add("jakarta.servlet");
    packagesToIgnore.add("java.lang.thread");

    packagesToIgnore.add("org.apache.hc.client5");
    packagesToIgnore.add("org.eclipse.jetty");
    packagesToIgnore.add("org.postgresql.core");
    packagesToIgnore.add("org.postgresql.jdbc");
  }

  public static StackTraceElement[] generateDisplayStackTrace(StackTraceElement[] errorTrace){

    List<StackTraceElement> displayItems = new ArrayList<StackTraceElement>();
    for(int i = 0; i < errorTrace.length; i++) {

      StackTraceElement element = errorTrace[i];
      boolean ignore = false;
      for (String ignoreItem : packagesToIgnore) {
        if (element.getClassName().contains(ignoreItem)) {
          ignore = true;
          break;
        }
      }
      if (!ignore) {
        displayItems.add(element);
      }
    }
    return displayItems.toArray(StackTraceElement[]::new);
  }
}
