package app.data;

public class LocationTag {

  private String name;
  private String[] locations;

  public LocationTag(){}

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String[] getLocations(){
    if(locations == null){
      return new String[0];
    }
    return locations;
  }

  public void setLocations(String[] locations){
    this.locations = locations;
  }
}
