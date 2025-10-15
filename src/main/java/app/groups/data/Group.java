package app.groups.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {

  public ArrayList<Event> events;
  public int id;
  public String url;
  public String[] cities;

  public String description;
  public String name;

  public String image;

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
    this.description = description;
  }

  public String getDescription(){
    return description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEvents(ArrayList<Event> events) {
    this.events = events;
  }

  public ArrayList<Event> getEvents() {
    return events;
  }

  public void setImage(String imageData){


    if(imageData != null){
      this.image = imageData.split(";")[1].substring(7);

      System.out.println(this.image.substring(0,999));
      try {


        byte[] base64Val= Base64.getDecoder().decode(this.image);

        File imgFile = new File("test.jpg");
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(base64Val));
        ImageIO.write(img, "jpg", imgFile);
      } catch(java.io.IOException e){
        e.printStackTrace();
      }

    }

  }
  public String getImage(){
    return this.image;
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
