package app.users.data;

import service.data.HtmlSanitizer;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.UUID;

public class UserData {
  private String username;

  private String image;
  private String imageFilePath;

  public void setUsername(String username){
    this.username = HtmlSanitizer.sanitizeTextOnly(username);
  }

  public String getUsername(){
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

        this.imageFilePath = "groups/"+days + "/image" + UUID.randomUUID()+".jpg";
      }
    }
  }

  public String getImage(){
    return this.image;
  }

  public void setImageFilePath(String imageFilePath){
    this.imageFilePath = imageFilePath;
  }

  public String getImageFilePath(){
    return this.imageFilePath;
  }

}
