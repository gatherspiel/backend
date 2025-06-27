package app.groups;

import app.groups.data.Group;
import app.result.error.InvalidGroupParameterError;
import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.Context;

import java.lang.reflect.Field;
import java.util.*;

public class GroupRequestParser {

  public static Group getGroupFromRequestBody(Context ctx) throws Exception{

    JsonNode groupJson = ctx.bodyAsClass(JsonNode.class);

    List<String> groupFields =
        Arrays.stream(Group.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
    Set<String> invalidFields = new HashSet<String>();
    groupJson.fieldNames().forEachRemaining(fieldName->{
      if(!groupFields.contains(fieldName)){
        invalidFields.add(fieldName);
      }
    });


    if(invalidFields.isEmpty()){
      return ctx.bodyAsClass(Group.class);
    } else {
      var errorMessage = "";
      if(invalidFields.size() > 1){
        errorMessage = "Invalid fields: "+String.join(",", invalidFields);
      } else {
        errorMessage = "Invalid field: " + invalidFields.stream().findFirst().get();
      }
      throw new InvalidGroupParameterError(errorMessage);
    }
  }
}
