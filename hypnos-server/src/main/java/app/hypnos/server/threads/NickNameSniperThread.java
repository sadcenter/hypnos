package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.SniperUtil;
import lombok.SneakyThrows;

public final class NickNameSniperThread extends Thread {

    private final Server server;

    public NickNameSniperThread(Server server) {
        this.server = server;

        setDaemon(true);
        setName("sniper");
    }

    @Override
    @SneakyThrows
    public void run() {
        for (User user : this.server.getUsers()) {
            for (Snipe snipe : user.getSnipes()) {
                String authToken = SniperUtil.getAuthToken(snipe.getAccount());
                if (snipe.getAccessTime() - 1500 <= System.currentTimeMillis()) {
                    for (int i = 0; i < 5; i++) {
                        SniperUtil.changeName(user, snipe, authToken);
                    }

                }
            }
        }

        Thread.sleep(10L);

        run();
    }
}
