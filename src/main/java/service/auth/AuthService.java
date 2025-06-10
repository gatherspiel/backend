package service.auth;

import app.data.auth.User;
import app.data.auth.UserType;
import app.request.BulkUpdateInputRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import database.user.UserRepository;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.Logger;
import service.data.AuthRequest;
import utils.LogUtils;
import utils.Params;

import java.util.Optional;

public class AuthService {

  private AuthProvider authProvider;
  private UserRepository userRepository;
  private static final Logger logger = LogUtils.getLogger();

  public AuthService(AuthProvider authProvider, UserRepository userRepository){
    this.authProvider = authProvider;
    this.userRepository = userRepository;
  }

  /**
   *
   * @return Returns currently logged in user, or read only user if the user is not logged in.
   */
  public User getUser(Context ctx, ConnectionProvider connectionProvider) throws Exception{
    logger.info("Retrieving current user");

    Optional<String> username =  authProvider.getUsernameFromToken(ctx.header("authToken"));

    if(!username.isPresent()){
      return getReadOnlyUser();
    }

    return userRepository.getUserFromEmail(username.get(), connectionProvider.getDatabaseConnection());
  }


  /**
   *
   * @return Returns read only user
   */
  public static User getReadOnlyUser(){
    User user = new User("reader@dmvboardgames.com", UserType.READONLY, 123);
    return user;
  }


  /**
   * Validate bulk update request used for internet testing
   * @param request
   * @return
   * @throws Exception
   */
  public boolean validateBulkUpdateInputRequest(BulkUpdateInputRequest request) throws Exception {
    final HttpPost httpPost = new HttpPost(Params.getSupabasePasswordCheckUrl());
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("apikey", Params.getSupabaseApiKey());
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
