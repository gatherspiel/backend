package app.groups;

import app.groups.data.Group;
import app.result.error.GroupNotFoundError;
import app.result.error.InvalidGroupParameterError;
import app.result.error.InvalidGroupRequestError;
import app.result.error.PermissionError;
import app.groups.data.GroupPageData;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import service.auth.AuthService;
import service.data.SearchParameterException;
import service.provider.ReadGroupDataProvider;
import service.read.ReadGroupService;
import service.update.GroupEditService;
import utils.LogUtils;

public class GroupsApi {

  public static Logger logger = LogUtils.getLogger();
  public static String GROUP_ID_PARAM = "id";

  public static void groupEndpoints(Javalin app) {
    app.get(
        "/groups",
        ctx -> {

          try {
            var connectionProvider = new ConnectionProvider();
            var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
                ctx
            );

            var currentUser = AuthService.getUser(connectionProvider.getDatabaseConnection(), ctx);
            logger.info("Current user:"+currentUser);

            var readGroupDataProvider = ReadGroupDataProvider.create();
            var groupService = new ReadGroupService(readGroupDataProvider);

            GroupPageData pageData = groupService.getGroupPageData(currentUser, searchParams, connectionProvider);
            logger.info("Retrieved group data");
            ctx.json(pageData);
            ctx.status(200);
          } catch (SearchParameterException e) {
            e.printStackTrace();
            ctx.status(404);
          } catch(Exception e){
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(500);
          }
        }
    );

    app.put(
      "/groups",
      ctx -> {

        Group group = null;
        try {
          var connectionProvider = new ConnectionProvider();

          var currentUser = AuthService.getUser(connectionProvider.getDatabaseConnection(), ctx);
          var groupEditService = new GroupEditService();

          group = ctx.bodyAsClass(Group.class);

          groupEditService.editGroup(currentUser,group, connectionProvider);

          logger.info("Updated group:"+group.id);
          ctx.status(200);
        } catch (UnrecognizedPropertyException | GroupNotFoundError | InvalidGroupRequestError e) {
          logger.error(e.getMessage());
          ctx.status(400);
          ctx.result(e.getMessage());
        } catch(PermissionError e) {
          ctx.status(403);
          logger.error(e.getMessage());
          ctx.result(e.getMessage());
        }
        catch(Exception e){
          e.printStackTrace();

          ctx.status(500);
        }
      }
    );

    app.post(
        "/groups",
        ctx -> {

          try {
            Group groupToCreate = GroupRequestParser.getGroupFromRequestBody(ctx);

            var connectionProvider = new ConnectionProvider();
            var currentUser = AuthService.getUser(connectionProvider.getDatabaseConnection(), ctx);
            var groupEditService = new GroupEditService();

            Group createdGroup = groupEditService.insertGroup(currentUser,groupToCreate, connectionProvider);

            logger.info("Created group group with id:"+createdGroup.id);
            ctx.json(createdGroup);
            ctx.status(200);
          } catch (GroupNotFoundError | InvalidGroupRequestError e) {
            logger.error(e.getMessage());
            ctx.status(400);
            ctx.result(e.getMessage());
          } catch(PermissionError e) {
            ctx.status(403);
            logger.error(e.getMessage());
            ctx.result(e.getMessage());
          } catch (InvalidGroupParameterError e) {
            logger.error(e.getMessage());
            ctx.status(400);
            ctx.result(e.getMessage());
          }
          catch(Exception e){
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(500);
          }
        }
    );

  app.delete(
    "/groups",
    ctx -> {

      try {
        var connectionProvider = new ConnectionProvider();
        var currentUser = AuthService.getUser(connectionProvider.getDatabaseConnection(), ctx);

        int groupId = Integer.parseInt(ctx.queryParam(GROUP_ID_PARAM));
        var groupEditService = new GroupEditService();

        groupEditService.deleteGroup(currentUser,groupId, connectionProvider);

        logger.info("Deleted group");
        ctx.status(200);
      }
      catch(Exception e){
        e.printStackTrace();
        ctx.result(e.getMessage());
        ctx.status(500);
      }
    }
    );
  }
}
