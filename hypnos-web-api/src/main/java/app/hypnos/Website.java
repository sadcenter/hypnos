package app.hypnos;

import com.google.gson.JsonObject;
import io.javalin.Javalin;

public final class Website {

    public static void main(String[] args) {
        Javalin javalin = Javalin.create().start(4893);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("ip", "95.214.52.221");
        jsonObject.addProperty("port", 5482);
        jsonObject.addProperty("version", 31);

        javalin.get("api", context -> {

            context.result(jsonObject.toString());
        });
    }

}
