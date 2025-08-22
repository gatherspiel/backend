package app;

import app.cache.CacheConnection;
import app.groups.GroupRequestParser;
import app.groups.data.Group;
import app.result.error.group.DuplicateGroupNameError;
import app.result.error.group.GroupNotFoundError;
import app.result.error.group.InvalidGroupParameterError;
import app.result.error.group.InvalidGroupRequestError;
import app.result.error.PermissionError;
import app.groups.data.GroupPageData;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import database.search.GroupSearchParams;
import database.utils.ConnectionProvider;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.apache.logging.log4j.Logger;
import service.data.SearchParameterException;
import utils.LogUtils;

import java.util.Optional;

public class GroupsApi {

  public static Logger logger = LogUtils.getLogger();
  public static String GROUP_ID_PARAM = "id";

  public static void groupEndpoints(Javalin app) {
    app.get(
      "/groups",
      ctx -> {

        try {

          var sessionContext = SessionContext.createContextWithUser(ctx, new ConnectionProvider());

          Optional<GroupPageData> data = Optional.empty();
          CacheConnection cacheConnection = new CacheConnection(ctx);

          //Only read from
          if(!sessionContext.getUser().isLoggedInUser()) {

            data = cacheConnection.getCachedGroupPage();
          }
          if(data.isEmpty()){
            var searchParams = GroupSearchParams.generateParameterMapFromQueryString(
                ctx
            );
            var groupService = sessionContext.createReadGroupService();
            data = Optional.of(groupService.getGroupPageData(searchParams));

            if(!sessionContext.getUser().isLoggedInUser()){
              cacheConnection.cacheGroupPage(data.get());
            }
          }
          ctx.json(data.get());
          ctx.status(HttpStatus.OK);
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
          ctx.status(HttpStatus.OK);
        } catch (UnrecognizedPropertyException | GroupNotFoundError | InvalidGroupRequestError e) {
          logger.error(e.getMessage());
          ctx.status(HttpStatus.BAD_REQUEST);
          ctx.result(e.getMessage());
        } catch(PermissionError e) {
          ctx.status(HttpStatus.UNAUTHORIZED);
          logger.error(e.getMessage());
          ctx.result(e.getMessage());
        }
        catch(Exception e){
          e.printStackTrace();
          ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
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
            ctx.status(HttpStatus.OK);
          } catch (GroupNotFoundError | InvalidGroupRequestError e) {
            logger.error(e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
          } catch(PermissionError e) {
            ctx.status(HttpStatus.FORBIDDEN);
            logger.error(e.getMessage());
            ctx.result(e.getMessage());
          } catch (InvalidGroupParameterError  | DuplicateGroupNameError e) {
            logger.error(e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result(e.getMessage());
          }
          catch(Exception e){
            e.printStackTrace();
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
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
        ctx.status(HttpStatus.OK);
      }
      catch(Exception e){
        e.printStackTrace();
        ctx.result(e.getMessage());
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    );
  }
}
