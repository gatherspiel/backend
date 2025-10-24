package app.groups.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import service.data.HtmlSanitizer;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {

  public ArrayList<Event> events;
  public int id;
  public String url;
  public String[] cities;

  public String description;
  public String name;

  public String image;
  public String imageFilePath;

  public GameTypeTag[] gameTypeTags;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setCities(String[] cities) {
    this.cities = cities;
  }

  public String[] getCities() {
    return cities;
  }

  public void setDescription(String description) {
    this.description = HtmlSanitizer.sanitizeHtml(description);
  }

  public String getDescription(){
    return description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = HtmlSanitizer.sanitizeTextOnly(name);
  }

  public void setEvents(ArrayList<Event> events) {
    this.events = events;
  }

  public ArrayList<Event> getEvents() {
    return events;
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

  public String toString() {
    return (
      "Events:" +
          events +
      "\n" +
      "id:" +
      id +
      "\n" +
      "url:" +
      url +
      "\n" +
      "cities:" +
      cities +
      "\n" +
      "description:" +
          description +
      "\n" +
      "name:" +
      name +
      "\n"
    );
  }

  public int countEvents() {
    if (events == null) {
      return 0;
    }
    return events.size();
  }

  public void addEvent(Event event) {
    if (events == null) {
      events = new ArrayList<Event>();

    }
    events.add(event);
  }


  public void addCity(String city) {

    city = HtmlSanitizer.sanitizeTextOnly(city);
    if(city == null){
      return;
    }

    if (cities == null) {
      cities = new String[] { city };
      return;
    }

    //TODO: Consider using set to improve efficiency.
    for(String existing: cities){
      if(existing.equals(city)){
        return;
      }
    }

    String[] updated = new String[cities.length + 1];
    System.arraycopy(cities, 0, updated, 0, cities.length);
    updated[cities.length] = city;
    cities = updated;
  }

  @JsonSetter("gameTypeTags")
  public void setTags(String[] tags){

    this.gameTypeTags = new GameTypeTag[tags.length];
    for(int i=0; i < tags.length; i++){
      gameTypeTags[i] = GameTypeTag.valueOf(tags[i].replace(" ","_").toUpperCase());
    }
  }

  public void setGameTypeTags(GameTypeTag[] gameTypeTags){
    this.gameTypeTags = gameTypeTags;
  }

  public GameTypeTag[] getGameTypeTags(){
    if(gameTypeTags == null){
      return new GameTypeTag[0];
    }
    return gameTypeTags;
  }
}
