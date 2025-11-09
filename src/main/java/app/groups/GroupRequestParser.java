package app.groups;

import app.result.error.StackTraceShortener;
import app.result.error.group.InvalidGroupParameterError;
import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.Context;

import java.lang.reflect.Field;
import java.util.*;

public class GroupRequestParser {

  public static Group getGroupFromRequestBody(Context ctx) throws Exception {

    try {

      JsonNode groupJson = ctx.bodyAsClass(JsonNode.class);

      List<String> groupFields =
          Arrays.stream(Group.class.getDeclaredFields())
              .map(Field::getName)
              .toList();
      Set<String> invalidFields = new HashSet<String>();
      groupJson.fieldNames().forEachRemaining(fieldName -> {
        if (!groupFields.contains(fieldName)) {
          invalidFields.add(fieldName);
        }
      });


      if (invalidFields.isEmpty()) {
        return ctx.bodyAsClass(Group.class);
      } else {
        var errorMessage = "";
        if (invalidFields.size() > 1) {
          errorMessage = "Invalid fields: " + String.join(",", invalidFields);
        } else {
          errorMessage = "Invalid field: " + invalidFields.stream().findFirst().get();
        }
        throw new InvalidGroupParameterError(errorMessage);
      }
    } catch (Exception e) {
      var error = new InvalidGroupParameterError(e.getMessage());
      error.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      throw error;
    }
  }
}
