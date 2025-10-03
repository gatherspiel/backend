package app.feedback;

public class Feedback {

  private String email;
  private String feedbackText;
  private String feedbackType;
  private String name;

  public String getEmail(){
    return email;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public String getFeedbackText(){
    return feedbackText;
  }

  public void setFeedbackText(String feedbackText){
    this.feedbackText = feedbackText;
  }

  public String getFeedbackType(){
    return feedbackType;
  }

  public void setFeedbackType(String feedbackType){
    this.feedbackType = feedbackType;
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }
}
