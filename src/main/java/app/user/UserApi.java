package app.user;

import app.data.auth.UserType;
import app.user.data.RegisterUserRequest;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import service.auth.AuthService;

public class UserApi {
  public static void createUserEndpoints(Javalin app){
    app.post(
        "/registerUser",
        ctx -> {

          try {
            var connectionProvider = new ConnectionProvider();
            var authService = AuthService.createSupabaseAuthService(connectionProvider.getDatabaseConnection());

            var data = ctx.bodyAsClass(RegisterUserRequest.class);
            var response = authService.registerUser(data, UserType.USER);

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
