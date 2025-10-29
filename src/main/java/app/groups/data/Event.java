package app.groups.data;

import app.users.data.PermissionName;
import app.users.data.User;
import app.users.data.UserData;
import app.users.data.UserComparator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.data.HtmlSanitizer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static utils.Params.IMAGE_BUCKET_URL;

public class Event {
  private Integer id;
  private String description;
  private String name;
  private String url;
  private LocalDate startDate;
  private LocalDate endDate;

  private EventLocation eventLocation;

  private String groupName;
  private Integer groupId;

  private HashMap<PermissionName, Boolean> permissions;

  private boolean isRecurring = false;
  private LocalTime startTime;
  private LocalTime endTime;
  private DayOfWeek dayOfWeek;

  private String image;
  private String imageFilePath;
  private String imageBucketKey;

  private Integer rsvpCount = 0;
  private boolean userHasRsvp = false;

  private TreeSet<User> moderators = new TreeSet<>(new UserComparator());

  public Event() {
    permissions = new HashMap<>();
  }

  public void setIsRecurring(boolean isRecurring){
    this.isRecurring = isRecurring;
  }

  public boolean getIsRecurring(){
    return isRecurring;
  }
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setDay(String day){
    if(day == null){
      return;
    }
    this.dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
  }

  public DayOfWeek getDay(){
    return dayOfWeek;
  }

  public void setStartTime(LocalTime startTime){
    this.startTime = startTime;
  }


  public LocalTime getStartTime(){
    if(startTime == null){
      return LocalTime.MIN;
    }
    return startTime.truncatedTo(ChronoUnit.SECONDS);
  }

  public void setEndTime(LocalTime endTime){
    this.endTime = endTime;
  }

  public LocalTime getEndTime(){
    if(endTime == null){
      return LocalTime.MAX;
    }
    return endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  @JsonIgnore
  public LocalDateTime getPrevious(){
    LocalDateTime previousEvent = LocalDateTime.now();

    if(dayOfWeek == null || startTime == null){
      return LocalDateTime.now().minusYears(1);
    }

    if(previousEvent.getDayOfWeek() != dayOfWeek){
      previousEvent = previousEvent.with(TemporalAdjusters.previous(dayOfWeek));
    }
    previousEvent = previousEvent.withHour(startTime.getHour());
    previousEvent = previousEvent.withMinute(startTime.getMinute());
    previousEvent = previousEvent.withSecond(startTime.getSecond());

    return previousEvent;
  }

  public String getLocation() {
    return eventLocation.toString();
  }

  public void setLocation(String location) throws Exception {

    if(location == null || location.isEmpty()){
      this.eventLocation = new EventLocation();
      return;
    }

    EventLocation eventLocation = new EventLocation();
    this.eventLocation = eventLocation;
    String[] locationSplit = location.split(",");

    if(locationSplit.length == 3) {
      String[] locationSplit2 = locationSplit[2].trim().split(" ");
      if(locationSplit2.length == 2) {
        eventLocation.setStreetAddress(locationSplit[0].trim());
        eventLocation.setCity(locationSplit[1].trim());
        eventLocation.setState(locationSplit2[0].trim());
        eventLocation.setZipCode(Integer.parseInt(locationSplit2[1].trim()));
        return;
      }
    }
    eventLocation.setStreetAddress(location);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {

    this.description = HtmlSanitizer.sanitizeHtml(description);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = HtmlSanitizer.sanitizeTextOnly(name);
  }

  public String getUrl(){
    return url;
  }

  public void setUrl(String url){
    this.url = url;
  }

  public void setStartDate(LocalDate startDate){
    this.startDate = startDate;
  }

  public LocalDate getStartDate(){
    return startDate;
  }

  public void setEndDate(LocalDate endDate){
    this.endDate = endDate;
  }

  public LocalDate getEndDate(){
    return endDate;
  }

  /*
 The start and end time are represented as strings as a workaround for a serialization limitation with the
 LocalDateTime object
  */
  @JsonGetter("startTime")
  public String getSerializedStartTime(){
    return startTime.toString();
  }

  @JsonGetter("endTime")
  public String getSerializedEndTime(){
    return endTime.toString();
  }

  @JsonProperty(required = false)
  public EventLocation getEventLocation(){
    return eventLocation;
  }

  public void setEventLocation(EventLocation eventLocation){
    this.eventLocation = eventLocation;
  }

  public void setGroupName(String groupName){
    this.groupName = HtmlSanitizer.sanitizeTextOnly(groupName);
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupId(Integer groupId){
    this.groupId = groupId;
  }

  public Integer getGroupId(){
    if(groupId == null){
      return -1;
    }
    return groupId;
  }

  public void setPermissions(HashMap<PermissionName, Boolean> permissions){
    this.permissions = permissions;
  }

  public HashMap<PermissionName, Boolean> getPermissions(){
    return permissions;
  }

  public void setUserCanEditPermission(Boolean canEdit){
    permissions.put(PermissionName.USER_CAN_EDIT, canEdit);
  }

  public void setImage(String imageData){
    if(imageData != null){
      var imageSplit = imageData.split(";");

      if(imageSplit.length == 1){
        this.image = imageData;
      } else {
        this.image = imageSplit[1].substring(7);

        var imageType = imageSplit[0].split("image/")[1];
        if(!imageType.equals("jpeg")){
          throw new RuntimeException("Unsupported image type:"+imageType);
        }
        LocalDate current = LocalDate.now();
        long days = current.getLong(ChronoField.EPOCH_DAY);
        this.imageBucketKey = "groups/events/"+days + "/image" + UUID.randomUUID()+".jpg";
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

  public String getImageFilePath(){
    return this.imageFilePath;
  }

  @JsonIgnore
  public String getImageBucketKey(){
    return this.imageBucketKey;
  }

  public void addModerator(User user){
    this.moderators.add(user);
  }

  public void setModerators(Set<User> updatedModerators){
    TreeSet<User> updated = new TreeSet<>(new UserComparator());
    updated.addAll(updatedModerators);
    this.moderators = updated;
  }

  public TreeSet<User> getModerators(){
    return moderators;
  }

  public void setRsvpCount(int rsvpCount){
    this.rsvpCount = rsvpCount;
  }

  public int getRsvpCount(){
    return rsvpCount;
  }

  public void setUserHasRsvp(boolean userHasRsvp){
    this.userHasRsvp = userHasRsvp;
  }

  public boolean getUserHasRsvp(){
    return userHasRsvp;
  }

  public String toString(){
    return "Event data \n id:"+this.id +"\nday:"+this.dayOfWeek+"\n location:"+ this.eventLocation.toString() +"\n description:"+this.description +
        " \nname:"+this.name+"\n url:"+this.url + "\n startTime:"+this.startTime + "\n endTime:" + this.endTime + "\n startDate:"+this.startDate + "\n endDate:" + this.endDate;
  }
}
