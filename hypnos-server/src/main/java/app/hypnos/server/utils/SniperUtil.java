package app.hypnos.server.utils;

import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.utils.logging.LogType;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.fusesource.jansi.Ansi;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class SniperUtil {

    private static final long TIME = TimeUnit.DAYS.toMillis(37);

    public static String getAuthToken(String userName, String password) {
        HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.post("https://authserver.mojang.com/authenticate")
                .body(getAuthPayload(userName, password))
                .asJson();

        if (!jsonNodeHttpResponse.isSuccess()) {
            return "null";
        }

        JsonNode body = jsonNodeHttpResponse.getBody();

        return body.getObject().getString("accessToken");
    }

    public static UUID getUniqueId(String name) {
        HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get("https://api.ashcon.app/mojang/v2/user/" + name).asJson();

        if (!jsonNodeHttpResponse.isSuccess()) {
            return null;
        }

        return UUID.fromString(jsonNodeHttpResponse.getBody().getObject().getString("uuid"));
    }

    public static String getOwner(String nowName) {
        HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get("https://api.mojang.com/users/profiles/minecraft/" + nowName + "?at=" + (System.currentTimeMillis() - TIME)).asJson();
        if (!jsonNodeHttpResponse.isSuccess()) {
            System.out.println(jsonNodeHttpResponse.getStatusText());
            return null;
        }

        System.out.println(jsonNodeHttpResponse.getBody().toPrettyString());

        return jsonNodeHttpResponse.getBody().getObject().getString("name");
    }

    public static String getLatestName(String name) {
        HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get("https://api.ashcon.app/mojang/v2/user/" + name).asJson();

        JSONObject object = jsonNodeHttpResponse.getBody().getObject();

        if (object.isNull("username_history") || !jsonNodeHttpResponse.isSuccess()) {
            return null;
        }


        JSONArray history = object.getJSONArray("username_history");
        return history.getJSONObject(history.length() - 2).getString("username");
    }

    public static long getAccessTime(String userName) {
        HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get("https://api.ashcon.app/mojang/v2/user/" + userName).asJson();

        JSONObject object = jsonNodeHttpResponse.getBody().getObject();

        if (object.isNull("username_history"))
            return 0L;

        JSONArray history = object.getJSONArray("username_history");

        return Instant.parse(history.getJSONObject(history.length() - 1).getString("changed_at")).toEpochMilli() + TIME;
    }

    public static void changeName(User user, Snipe snipe) {
        HttpResponse<JsonNode> authorization = Unirest.get("https://api.minecraftservices.com/minecraft/profile/name/" + snipe.getName())
                .header("Authorization", "Bearer " + getAuthToken(snipe.getAccount().getUserName(), snipe.getAccount().getPassword()))
                .asJson();

        int status = authorization.getStatus();

        String message;
        if (status == 403) {
            message = "Name already taken ;/";
        } else if (status == 401) {
            message = "Unauthorized";
        } else if (status == 200) {
            message = "Changed name (" + snipe.getName() + ")";
        } else {
            message = authorization.getStatusText();
        }

        user.sendMessage("[" + snipe.getName() + "] " + message, Ansi.Color.MAGENTA, LogType.INFO);
        user.getLogs().add(message);
        user.setUpdateRequired(true);
    }

    private static String getAuthPayload(String userName, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", userName);
        jsonObject.addProperty("password", password);
        return jsonObject.toString();
    }

}
