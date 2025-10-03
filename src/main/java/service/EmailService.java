package service;

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

  private static final Resend RESEND_CLIENT = new Resend(EMAIL_KEY);

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
      CreateEmailResponse data = RESEND_CLIENT.emails().send(params);
    } catch (ResendException e) {
      e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
      e.printStackTrace();
      logger.warn("Failed to send email for creating group");
    }
  }

}
