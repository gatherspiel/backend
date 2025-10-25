package app.users.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import service.data.HtmlSanitizer;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.UUID;

import static utils.Params.IMAGE_BUCKET_URL;

public class UserData {
  private String username;

  private String image;
  private String imageBucketKey;
  private String imageFilePath;
  public void setUsername(String username){
    this.username = HtmlSanitizer.sanitizeTextOnly(username);
  }

  public String getUsername(){
    if(username == null){
      return "";
    }
    return username;
  }

  public void setImage(String imageData){
    if(imageData != null){
      var imageSplit = imageData.split(";");

      if(imageSplit.length == 1){
        this.image = imageData;
      }
      else {
        this.image = imageSplit[1].substring(7);

        var imageType = imageSplit[0].split("image/")[1];
        if(!imageType.equals("jpeg")){
          throw new RuntimeException("Unsupported image type:"+imageType);
        }

        LocalDate current = LocalDate.now();
        long days = current.getLong(ChronoField.EPOCH_DAY);

        this.imageBucketKey = "users/"+days + "/image" + UUID.randomUUID()+".jpg";
        this.imageFilePath = IMAGE_BUCKET_URL + this.imageBucketKey;

      }
    }
  }

  public String getImage(){
    return this.image;
  }


  public void setImageFilePath(String imageFilePath){
    this.imageFilePath = imageFilePath;
  }


  @JsonIgnore
  public String getImageBucketKey(){
    return IMAGE_BUCKET_URL+this.imageBucketKey;
  }

  public String getImageFilePath(){
    return this.imageFilePath;
  }

}
