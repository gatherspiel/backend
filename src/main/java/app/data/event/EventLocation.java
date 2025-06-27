package app.data.event;

public class EventLocation {
  private String city;
  private EventLocationState state;
  private String streetAddress;
  private Integer zipCode;

  public void setCity(String city){
    this.city = city;
  }

  public String getCity(){
    return city;
  }

  public void setState(String eventLocationState) throws Exception{
    this.state = EventLocationState.fromString(eventLocationState);
  }

  public String getState(){
    return state.toString();
  }

  public void setStreetAddress(String streetAddress){
    this.streetAddress = streetAddress;
  }

  public String getStreetAddress(){
    return streetAddress;
  }

  public void setZipCode(Integer zipCode){
    this.zipCode = zipCode;
  }

  public Integer getZipCode(){
    return zipCode;
  }
}
