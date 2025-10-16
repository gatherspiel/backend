package database.files;

import app.result.error.ImageUploadException;
import app.result.error.StackTraceShortener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.InputStream;
import java.util.Base64;

public class ImageRepository {

  private final String BUCKET_URL;
  private static final Logger logger = LogUtils.getLogger();

  public class ImageUploadRequest{
    public byte[] imageData;
  }
  public ImageRepository(){
    var bucketKey = System.getenv("IMAGE_BUCKET_KEY");

    BUCKET_URL = "https://gatherspiel.nyc3.digitaloceanspaces.com/"+bucketKey;
  }
  public void uploadImage(String imageData){

    try {
      byte[] base64Val= Base64.getDecoder().decode(imageData);

      ImageUploadRequest request = new ImageUploadRequest();
      request.imageData = base64Val;

      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String json = ow.writeValueAsString(request);

      final HttpPut httpPut = new HttpPut(BUCKET_URL);
      HttpEntity entity = new StringEntity(
          json,
          ContentType.IMAGE_JPEG
      );
      httpPut.setEntity(entity);

      CloseableHttpClient httpClient = HttpClients.createDefault();
      JsonNode httpResponse = httpClient.execute(httpPut, response -> {

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
      httpResponse.fieldNames().forEachRemaining(item->logger.info(item));

    } catch(Exception e) {
      var exception = new ImageUploadException(e.getMessage());
      exception.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      logger.error(e.getMessage());
      throw exception;
    }

  }
}
