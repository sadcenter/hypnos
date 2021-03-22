package app.hypnos.server.data;

import lombok.Data;

@Data
public final class Snipe {

    private final String name;
    private final Account account;
    private final long accessTime;

}
