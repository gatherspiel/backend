package app;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import utils.LogUtils;

public class UsersApi {
  public static Logger logger = LogUtils.getLogger();

  public static void userEndpoints(Javalin app){
    app.post(
        "/users/register",
        ctx -> {

          try {

            var response = AuthService.registerUser(ctx);

            logger.info("User created successfully");
            ctx.status(HttpStatus.OK);
            ctx.json(response);
          } catch(AuthService.RegisterUserInvalidDataException e){
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result(e.getMessage());
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
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
    );
  }
}
