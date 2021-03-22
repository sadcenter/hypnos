package app.hypnos.server.data;

import lombok.Data;

@Data
public final class Ban {

    private final String reason;
    private final String admin;

}
