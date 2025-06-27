package app;

import app.groups.GroupRequestParser;
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
import service.data.SearchParameterException;
import utils.LogUtils;

public class GroupsApi {

  public static Logger logger = LogUtils.getLogger();
  public static String GROUP_ID_PARAM = "id";

  public static void groupEndpoints(Javalin app) {
    app.get(
        "/groups",
        ctx -> {

          try {

            var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
            var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
                ctx
            );
            var groupService = sessionContext.createReadGroupService();

            GroupPageData pageData = groupService.getGroupPageData(searchParams);
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

          var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
          var groupEditService = sessionContext.createGroupEditService();

          group = ctx.bodyAsClass(Group.class);
          groupEditService.editGroup(group);

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

            var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());
            var groupEditService = sessionContext.createGroupEditService();
            Group createdGroup = groupEditService.insertGroup(groupToCreate);

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
        var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());

        int groupId = Integer.parseInt(ctx.queryParam(GROUP_ID_PARAM));
        var groupEditService = sessionContext.createGroupEditService();
        groupEditService.deleteGroup(groupId);

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
