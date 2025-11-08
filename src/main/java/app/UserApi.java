package app;

import app.result.error.DuplicateUsernameException;
import app.result.error.RegisterUserInvalidEmailException;
import app.result.error.StackTraceShortener;
import app.result.error.UnauthorizedError;
import app.users.SessionContext;
import app.users.UserData;
import app.users.UserMemberData;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import service.auth.UserService;
import service.update.UserMemberService;
import utils.LogUtils;

public class UserApi {
  public static Logger logger = LogUtils.getLogger();

  public static void userEndpoints(Javalin app){

    app.post(
      "/user/register",
      ctx -> {

        try {
          var response = AuthService.registerUser(ctx);
          logger.info("User created successfully");
          ctx.status(HttpStatus.OK);
          ctx.json(response);
        } catch(AuthService.RegisterUserInvalidDataException e) {
          ctx.status(HttpStatus.BAD_REQUEST);
          ctx.result(e.getMessage());
        }
        catch (DuplicateUsernameException e){
          e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
          e.printStackTrace();
          ctx.status(HttpStatus.BAD_REQUEST);
          ctx.result(e.getMessage());
        } catch (RegisterUserInvalidEmailException e) {
          ctx.status(HttpStatus.BAD_REQUEST);
          ctx.result("Invalid email address");
        }

        catch(MismatchedInputException e){
          if(e.getMessage().contains("app.user.data.RegisterUserRequest")){
            ctx.result(ctx.body() + " is not valid input for the POST /users/register endpoint");
            ctx.status(HttpStatus.BAD_REQUEST);
          } else {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }catch (Exception e){
          e.setStackTrace(StackTraceShortener.generateDisplayStackTrace(e.getStackTrace()));
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    );

    app.get(
      "user",
      ctx ->{
        try {
          SessionContext sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          UserService userService = sessionContext.createUserService();

          UserData userData = userService.getLoggedInUserData();
          ctx.status(200);
          ctx.json(userData);
        } catch(UnauthorizedError e){
          logger.error(e.getMessage());
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.UNAUTHORIZED);
        } catch(Exception e){
          logger.error(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          ctx.result(e.getMessage());
        }
      }
    );

    app.put(
      "/user/",
      ctx ->{

        try {
          SessionContext sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          UserData userData = ctx.bodyAsClass(UserData.class);
          UserService userService = sessionContext.createUserService();

          userService.updateUser(userData);

          ctx.result("Updated user data");
          ctx.status(200);
        } catch(UnauthorizedError e){
          logger.error(e.getMessage());
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.UNAUTHORIZED);
        } catch(Exception e){
          logger.error(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          ctx.result(e.getMessage());
        }
      }
    );

    app.get(
      "/user/memberData/",
      ctx ->{
        try {
          SessionContext sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          UserMemberService userMemberService = sessionContext.createUserMemberService();
          UserMemberData memberData = userMemberService.getUserMemberData();
          ctx.json(memberData);
          ctx.status(200);
        } catch(UnauthorizedError e){
          logger.error(e.getMessage());
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.UNAUTHORIZED);
        } catch(Exception e){
          logger.error(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          ctx.result(e.getMessage());
        }
      }
    );

    app.post(
      "/user/memberData/group/{groupId}",
      ctx ->{
        try {
          var groupId = Integer.parseInt(ctx.pathParam("groupId"));

          SessionContext sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          UserMemberService userMemberService = sessionContext.createUserMemberService();

          userMemberService.joinGroup(groupId);
          ctx.result("Successfully joined group");
          ctx.status(200);
        } catch(UnauthorizedError e){
          logger.error(e.getMessage());
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.UNAUTHORIZED);
        } catch(Exception e){
          logger.error(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          ctx.result(e.getMessage());
        }
      }
    );

    app.delete(
      "/user/memberData/group/{groupId}",
      ctx ->{
        try {
          var groupId = Integer.parseInt(ctx.pathParam("groupId"));

          SessionContext sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          UserMemberService userMemberService = sessionContext.createUserMemberService();

          userMemberService.leaveGroup(groupId);
          ctx.result("Successfully left group");
          ctx.status(200);
        } catch(UnauthorizedError e){
          logger.error(e.getMessage());
          e.printStackTrace();
          ctx.result(e.getMessage());
          ctx.status(HttpStatus.UNAUTHORIZED);
        } catch(Exception e){
          logger.error(e.getMessage());
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          ctx.result(e.getMessage());
        }
      }
    );
  }
}
