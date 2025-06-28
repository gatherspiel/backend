package app;

import app.users.data.User;
import database.content.EventRepository;
import database.utils.ConnectionProvider;
import io.javalin.http.Context;
import service.auth.AuthService;
import service.permissions.GroupPermissionService;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.read.SearchService;
import service.update.EventService;
import service.update.GroupEditService;
import service.user.UserService;

import java.sql.Connection;

public class SessionContext {

  private User user;
  private final Connection conn;

  private SessionContext(Connection conn, User user) {
    this.user = user;
    this.conn = conn;
  }

  public EventService createEventService(){
    return new EventService(conn, new EventRepository(conn), createGroupPermissionService(), user);
  }

  public GroupPermissionService createGroupPermissionService(){
    return new GroupPermissionService(conn, user);
  }

  public UserService createUserService(){
    var dataProvider = UserService.DataProvider.createDataProvider(conn);
    return new UserService(dataProvider);
  }

  public ReadGroupService createReadGroupService() {
    System.out.println("User:"+user);
    var readGroupDataProvider = ReadGroupDataProvider.create(conn, user);
    return new ReadGroupService(readGroupDataProvider, conn);
  }

  public GroupEditService createGroupEditService() {
    return new GroupEditService(conn, user);
  }

  public SearchService createSearchService() {
    return new SearchService(conn);
  }

  public void setUser(User user){
    this.user = user;
  }

  public User getUser(){
    return user;
  }

  public Connection getDatabaseConnection() {
    return conn;
  }

  public static SessionContext createContextWithUser(Context ctx, ConnectionProvider provider) throws Exception{
    var conn = provider.getDatabaseConnection();
    var currentUser = AuthService.getUser(conn, ctx);

    return new SessionContext(conn, currentUser);
  }

  public static SessionContext createContextWithoutUser( ConnectionProvider provider) throws Exception {
    var conn = provider.getDatabaseConnection();
    var currentUser = AuthService.getReadOnlyUser();

    System.out.println(currentUser);
    return new SessionContext(conn, currentUser);
  }
}
