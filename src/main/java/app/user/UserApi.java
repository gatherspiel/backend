package app.user;

import app.data.auth.UserType;
import app.user.data.RegisterUserRequest;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import utils.LogUtils;

public class UserApi {
  public static Logger logger = LogUtils.getLogger();

  public static void createUserEndpoints(Javalin app){
    app.post(
        "/users/register",
        ctx -> {

          try {
            var connectionProvider = new ConnectionProvider();
            var authService = AuthService.createSupabaseAuthService(connectionProvider.getConnectionWithManualCommit());
            
            var data = ctx.bodyAsClass(RegisterUserRequest.class);
            var response = authService.registerUser(data, UserType.USER);

            logger.info("User created successfully");
            ctx.status(200);
            ctx.json(response);
          } catch(MismatchedInputException e){
            if(e.getMessage().contains("app.user.data.RegisterUserRequest")){
              ctx.result(ctx.body() + " is not valid input for the POST /users/register endpoint");
              ctx.status(400);
            } else {
              ctx.result(e.getMessage());
              ctx.status(500);
            }
          }catch (Exception e){
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(500);
          }
        }
    );
  }
}
