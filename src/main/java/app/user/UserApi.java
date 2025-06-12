package app.user;

import app.data.auth.UserType;
import app.user.data.RegisterUserRequest;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import utils.LogUtils;

public class UserApi {
  public static Logger logger = LogUtils.getLogger();

  public static void createUserEndpoints(Javalin app){
    app.post(
        "/user/register",
        ctx -> {

          try {
            var connectionProvider = new ConnectionProvider();
            var authService = AuthService.createSupabaseAuthService(connectionProvider.getConnectionWithManualCommit());

            System.out.println(ctx.body());

            var data = ctx.bodyAsClass(RegisterUserRequest.class);
            var response = authService.registerUser(data, UserType.USER);

            logger.info("User created successfully");
            ctx.status(200);
            ctx.json(response);
          } catch (Exception e){
            e.printStackTrace();
            ctx.status(500);
          }
        }
    );
  }
}
