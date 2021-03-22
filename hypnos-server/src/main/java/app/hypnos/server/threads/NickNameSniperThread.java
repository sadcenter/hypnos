package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.utils.SniperUtil;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public final class NickNameSniperThread extends Thread {

    private final Server server;

    public NickNameSniperThread(Server server) {
        this.server = server;

        setDaemon(true);
        setName("sniper");
    }

    @SneakyThrows
    @Override
    public void run() {
        this.server.getUsers().forEach(user -> {
            user.getSnipes().forEach(snipe -> {
                if (snipe.accessTime() - TimeUnit.SECONDS.toMillis(2) >= System.currentTimeMillis()) {
                    for (int i = 0; i < 20; i++) {
                        SniperUtil.changeName(user, snipe);
                    }
                }
            });
        });

        Thread.sleep(250L);
        run();
    }
}
