package service;

import app.feedback.Feedback;
import app.groups.data.Group;
import app.result.error.StackTraceShortener;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;
import app.users.data.User;

public class EmailService {

  private static final String EMAIL_KEY = System.getenv("EMAIL_KEY");
  private static final String NOTIFICATION_SENDER = "notifications <notifications@admin.dmvboardgames.com>";
  private static final String ADMIN_EMAIL = "gulu@createthirdplaces.com";

  private static final Logger logger = LogUtils.getLogger();
  private User user;

  public EmailService(User user){
    this.user = user;
  }

  public void sendGroupCreatedNotification(Group createdGroup){
    CreateEmailOptions params = CreateEmailOptions.builder()
        .from(NOTIFICATION_SENDER)
        .to(ADMIN_EMAIL)
        .subject("A group has been created")
        .html("<strong>User:"+user.getEmail() +" Group name:"+createdGroup.getName()+" Group description:"+createdGroup.getDescription()+ "</strong>")
        .build();
    try {
      final Resend resendClient = new Resend(EMAIL_KEY);
      CreateEmailResponse data = resendClient.emails().send(params);
    } catch (RuntimeException | ResendException e) {
      logger.warn("Failed to send email for creating group");
      if(("prod").equals(System.getenv("ENV"))) {
        e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
        e.printStackTrace();
      }
    }
  }

  public void sendFeedbackNotification(Feedback feedback) throws Exception{

    String feedbackHtml = "<b>Email:"+feedback.getEmail() +"</b>" +
        "<b>Feedback text:"+feedback.getFeedbackText() +"</b>" +
        "<b>Feedback type"+feedback.getFeedbackType() +"</b>" +
        "<b>Feedback username"+feedback.getName() +"</b>";

    if(feedbackHtml.length() > 15000){
      throw new Exception("Length limit for feedback exceeded");
    }

    CreateEmailOptions params = CreateEmailOptions.builder()
        .from(NOTIFICATION_SENDER)
        .to(ADMIN_EMAIL)
        .subject("Feedback has been submitted")
        .html(feedbackHtml)
        .build();
    try {
      final Resend resendClient = new Resend(EMAIL_KEY);
      CreateEmailResponse data = resendClient.emails().send(params);
    } catch (RuntimeException | ResendException e) {
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      e.printStackTrace();
      logger.warn("Failed to send feedback with contents:"+feedbackHtml);
    }
  }

}
