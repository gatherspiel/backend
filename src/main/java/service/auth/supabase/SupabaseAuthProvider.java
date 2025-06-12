package service.auth.supabase;

import app.user.data.RegisterUserRequest;
import app.user.data.RegisterUserResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.logging.log4j.Logger;
import service.auth.AuthProvider;
import utils.LogUtils;
import utils.Params;

import java.io.InputStream;
import java.util.Optional;

public class SupabaseAuthProvider implements AuthProvider {

  private static final Logger logger = LogUtils.getLogger();


  @Override
  public RegisterUserResponse registerUser(RegisterUserRequest request) throws Exception{

    String url = Params.getAuthUrl()+"signup";
    final HttpPost httpPost = new HttpPost(url);

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(request);

    logger.info("Register request JSON:"+json);

    HttpEntity stringEntity = new StringEntity(
        json,
        ContentType.APPLICATION_JSON
    );
    httpPost.setEntity(stringEntity);

    try {
      CloseableHttpClient httpClient = HttpClients.createDefault();

      JsonNode httpResponse = httpClient.execute(httpPost, response ->{
        ObjectMapper objectMapper = new ObjectMapper();


        final HttpEntity responseEntity = response.getEntity();

        JsonNode result;
        try (InputStream inputStream = responseEntity.getContent()) {
          result = objectMapper.readTree(inputStream);
        }

        if (response.getCode() >= 300) {

          if(result != null){
            throw new ClientProtocolException(result.toString());
          }
          throw new ClientProtocolException(new StatusLine(response).toString());
        }
        return result;

      });


      logger.info(httpResponse);

      JsonNode userData = httpResponse.get("user");
      String email = userData.get("email").textValue();
      String createdAt = userData.get("created_at").textValue();

      //TODO: Send email confirmation
      return new RegisterUserResponse(email, createdAt);

    } catch (Exception e){
      logger.error("Failed to register user with url:"+url);
      logger.error(e.getMessage());
      throw e;
    }
  }

  @Override
  public Optional<String> getUsernameFromToken(String token) throws Exception {
    System.out.println(Params.getAuthUrl());
    final HttpGet httpGet = new HttpGet(Params.getAuthUrl()+"user");
    httpGet.setHeader("Authorization", "Bearer "+token);

    try {
      CloseableHttpClient httpClient = HttpClients.createDefault();
      ObjectMapper objectMapper = new ObjectMapper();

      JsonNode httpResponse = httpClient.execute(httpGet, response-> {

        final HttpEntity responseEntity = response.getEntity();
        if (responseEntity == null) {
          return null;
        }

        JsonNode responseData;
        try (InputStream inputStream = responseEntity.getContent()) {
          responseData = objectMapper.readTree(inputStream);
        }

        if (response.getCode() >= 300) {
          logger.error(responseData.toPrettyString());
          throw new ClientProtocolException(new StatusLine(response).toString());
        }

        return responseData;
      });

      httpClient.close();

      logger.info("Authorized");
      String email = httpResponse.get("email").textValue();

      return Optional.of(email);
    } catch (Exception e) {
      logger.error("[AuthService.java] Authorization failed with error", e.getMessage());
      return Optional.empty();
    }
  }
}
