package database.files;

import app.result.error.ImageUploadException;
import app.result.error.StackTraceShortener;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

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
  public void uploadImage(String imageData,String filePath){

    try {
      
      String fileType = filePath.split("\\.")[1];

      AWSCredentials credentials = new BasicAWSCredentials(
        System.getenv("IMAGE_BUCKET_ID"),
        System.getenv("IMAGE_BUCKET_KEY")
      );

      var endpoint = new AwsClientBuilder.EndpointConfiguration("https://nyc3.digitaloceanspaces.com/", "us-east-1");

      AmazonS3 amazonS3 = AmazonS3ClientBuilder
          .standard()
          .withCredentials(new AWSStaticCredentialsProvider(credentials))
          .withEndpointConfiguration(endpoint)
          .build();

      byte[] base64Val= Base64.getDecoder().decode(imageData);

      var tmpPath = "image_"+UUID.randomUUID()+"."+fileType;
      File imgFile = new File(tmpPath);
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(base64Val));
      ImageIO.write(img, fileType, imgFile);

      var putObjectRequest = new PutObjectRequest(
        "gatherspiel",
        filePath,
        imgFile
      );

      amazonS3.putObject(putObjectRequest);

      if(!imgFile.delete()){
        throw new RuntimeException("Failed to delete temporary image file");
      }
    } catch(Exception e) {
      var exception = new ImageUploadException("Error uploading image");
      exception.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      logger.error(e.getMessage());
      throw exception;
    }

  }
}
