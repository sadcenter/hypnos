package app.hypnos.server.utils;

import app.hypnos.server.data.Account;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.utils.logging.LogType;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class SniperUtil {

    private static final long TIME = TimeUnit.DAYS.toMillis(37);


    private static final LoadingCache<Account, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build(snipe -> getAuthTokenUncached(snipe.getUserName(), snipe.getPassword()));


    @SneakyThrows
    private static String getAuthTokenUncached(String userName, String password) {
        HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.post("https://authserver.mojang.com/authenticate")
                .contentType("application/json")
                .body(getAuthPayload(userName, password))
                .asJsonAsync().get();

        if (!jsonNodeHttpResponse.isSuccess()) {
            return null;
        }

        return jsonNodeHttpResponse.getBody().getObject().getString("accessToken");
    }

    @SneakyThrows
    public static String getAuthToken(Account account) {
        return tokenCache.get(account);
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
            return null;
        }

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

        return (Instant.parse(history.getJSONObject(history.length() - 1).getString("changed_at")).toEpochMilli() + TIME);
    }

    @SneakyThrows
    public static int getViews(String name) {
        return Unirest.get("https://api.nathan.cx/searches/" + name)
                .asJsonAsync().get().getBody().getObject().getInt("searches");
    }

    @SneakyThrows
    public static void changeName(User user, Snipe snipe, String authToken) {
        HttpResponse<JsonNode> authorization = Unirest.put("https://api.minecraftservices.com/minecraft/profile/name/" + snipe.getName())
                .header("Authorization", "Bearer " + authToken)
                .asJsonAsync().get();

        int status = authorization.getStatus();

        String message;
        if (status == 403) {
            message = " not available yet ;/";
        } else if (status == 401) {
            message = "Unauthorized (Maybe you provided wrong login data)";
        } else if (status == 200) {
            message = "Changed name (" + snipe.getName() + ")";
            user.getSnipes().remove(snipe);
            user.setSuccessSnipes(user.getSuccessSnipes() + 1);
        } else {
            message = authorization.getStatusText();
        }

        if (getUniqueId(snipe.getName()) != null) {
            message = "Someone other changed name!";
            user.getSnipes().remove(snipe);
        }

        message = message + " [" + new Date().toString() + "] ";

        user.getLogs().add(authorization.getBody().toPrettyString());

        user.sendMessage("[" + snipe.getName() + "] " + message, Ansi.Color.MAGENTA, LogType.INFO);
        user.getLogs().add(message);
        user.setUpdateRequired(true);
    }

    private static String getAuthPayload(String userName, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", userName);
        jsonObject.addProperty("password", password);
        //jsonObject.addProperty("requestUser", true);
        //JsonObject agent = new JsonObject();
        //agent.addProperty("name", "Minecraft");
        //agent.addProperty("version", 1);
        //  jsonObject.add("agent", agent);
        return jsonObject.toString();
    }

}
