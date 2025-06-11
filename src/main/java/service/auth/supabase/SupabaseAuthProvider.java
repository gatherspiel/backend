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
  public RegisterUserResponse registerUser(String username, String password) throws Exception{

    final HttpPost httpPost = new HttpPost(Params.getAuthUrl()+"signup");

    var registerUserRequest = new RegisterUserRequest(username, password);
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(registerUserRequest);
    HttpEntity stringEntity = new StringEntity(
        json,
        ContentType.APPLICATION_JSON
    );
    httpPost.setEntity(stringEntity);

    try {
      CloseableHttpClient httpClient = HttpClients.createDefault();

      JsonNode httpResponse = httpClient.execute(httpPost, response ->{
        ObjectMapper objectMapper = new ObjectMapper();

        if (response.getCode() >= 300) {
          throw new ClientProtocolException(new StatusLine(response).toString());
        }
        final HttpEntity responseEntity = response.getEntity();
        try (InputStream inputStream = responseEntity.getContent()) {
          return objectMapper.readTree(inputStream);
        }

      });


      String email = httpResponse.get("email").textValue();
      String createdAt = httpResponse.get("created_at").textValue();

      //TODO: Send email confirmation
      return new RegisterUserResponse(email, createdAt);

    } catch (Exception e){
      logger.error("Failed to register user");
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
        if (response.getCode() >= 300) {
          throw new ClientProtocolException(new StatusLine(response).toString());
        }
        final HttpEntity responseEntity = response.getEntity();
        if (responseEntity == null) {
          return null;
        }
        try (InputStream inputStream = responseEntity.getContent()) {
          return objectMapper.readTree(inputStream);
        }
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
