package app.hypnos;

import app.hypnos.data.Default;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;

public class Website {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    private static final String defaultAPIResponse = GSON.toJson(new Default());

    public static void main(String[] args) {
        Javalin.create().start(4893).get("api", context -> context.result(defaultAPIResponse));
    }
}