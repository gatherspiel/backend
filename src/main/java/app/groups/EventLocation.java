package app.groups;

import service.data.HtmlSanitizer;

public class EventLocation {
  private String city;
  private EventLocationState state;
  private String streetAddress;
  private Integer zipCode;

  public void setCity(String city){
    this.city = HtmlSanitizer.sanitizeTextOnly(city);
  }

  public String getCity(){
    return city;
  }

  public void setState(String eventLocationState) throws Exception{
    if(eventLocationState == null){
      this.state = EventLocationState.fromString("TBD");
    } else {
      this.state = EventLocationState.fromString(eventLocationState);
    }
  }

  public String getState(){
    return state != null ? state.toString() : "";
  }

  public void setStreetAddress(String streetAddress){
    this.streetAddress = HtmlSanitizer.sanitizeTextOnly(streetAddress);
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

  public String toString(){
    if(city == null || state == null || zipCode == null){
      return streetAddress;
    }
    return streetAddress + "," + city + "," + state.toString() + " " + zipCode;
  }
}
