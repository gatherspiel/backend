package app;
import io.javalin.Javalin;
import service.TestService;

public class Main {
    public static void main(String[] args) {
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        app.get("/countLocations", ctx->{
            var testService = new TestService();
            ctx.result("" + testService.countLocations());

        });
    }
}