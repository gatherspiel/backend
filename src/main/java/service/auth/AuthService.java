package service.auth;

import app.data.auth.User;
import app.data.auth.UserType;
import app.request.BulkUpdateInputRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.logging.log4j.Logger;
import service.data.AuthRequest;
import service.user.UserService;
import utils.LogUtils;
import utils.Params;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AuthService {
  private static final Logger logger = LogUtils.getLogger();
  final String API_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImthcnF5c2t1dWRudmZ4b2h3a29rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE5ODQ5NjgsImV4cCI6MjA1NzU2MDk2OH0.TR-Pn6dknOTtqS9y-gxK_S1-nw6TX-sL3gRH2kXJY_I";
  final String URL =
    "https://karqyskuudnvfxohwkok.supabase.co/auth/v1/token?grant_type=password";

  private UserService userService;

  public AuthService(){
    userService = new UserService();
  }
  /**
   *
   * @return Returns currently logged in user, or read only user if the user is not logged in.
   */
  public User getCurrentUser(Context ctx, ConnectionProvider connectionProvider) throws Exception{
    logger.info("Retrieving current user");

    return getUserFromToken(ctx.header("authToken"), connectionProvider);
  }

  /**
   *
   * @return Returns read only user
   */
  public User getCurrentUser(){

    //TODO: Get current user from token
    User user = new User("reader@dmvboardgames.com", UserType.READONLY, 123);
    return user;
  }


  /*
   Make sure the token is valid. If the token is invalid, throw an error.
   If the token has expired, print a warning message
   */
  private boolean validateToken() throws Exception{
   return false;
  }

  public User getUserFromToken(String token, ConnectionProvider connectionProvider) throws Exception {
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

      return userService.getUser(email, connectionProvider);
    } catch (Exception e) {
      logger.info("Authorization failed with error", e);
      return getCurrentUser();
    }
  }


  public boolean validateBulkUpdateInputRequest(BulkUpdateInputRequest request) throws Exception {
    final HttpPost httpPost = new HttpPost(URL);
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("apikey", API_KEY);
    final HttpClient httpClient = HttpClients.createDefault();

    AuthRequest authRequest = new AuthRequest();

    authRequest.setPassword(request.getPassword());
    authRequest.setEmail(request.getEmail());

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(authRequest);

    HttpEntity stringEntity = new StringEntity(
      json,
      ContentType.APPLICATION_JSON
    );
    httpPost.setEntity(stringEntity);

    try {
      HttpResponse rawResponse = httpClient.execute(httpPost);

      final int statusCode = rawResponse.getCode();

      if (statusCode != 200) {
        logger.debug(rawResponse);

        throw new Exception(
          "Authorization failed with status code:" + statusCode
        );
      }
    } catch (Exception e) {
      logger.error("Authorization failed with error", e);
      throw (e);
    }
    logger.info("Authorized");
    return true;
  }
}
