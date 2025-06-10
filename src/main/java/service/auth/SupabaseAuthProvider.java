package service.auth;

import app.data.auth.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.utils.ConnectionProvider;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;
import utils.Params;

import java.io.InputStream;
import java.util.Optional;

public class SupabaseAuthProvider implements AuthProvider{

  private static final Logger logger = LogUtils.getLogger();

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
