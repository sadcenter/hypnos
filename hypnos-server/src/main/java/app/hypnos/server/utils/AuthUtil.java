package app.hypnos.server.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public final class AuthUtil {

    public static String generateAuthToken(String user, String pass) {
        return Hashing.sha256()
                .hashString(user + pass, StandardCharsets.UTF_8)
                .toString();
    }

}