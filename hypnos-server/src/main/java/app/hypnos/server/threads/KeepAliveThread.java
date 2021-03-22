package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.User;

public final class KeepAliveThread extends Thread {

    private final Server server;

    public KeepAliveThread(Server server) {
        this.server = server;

        setDaemon(true);
        setName("keep alive thread");
    }

    @Override
    public void run() {
        server.getConnectedUsers().stream().map(User::getChannel).forEach(channel -> {

            if (server.getKeepAliveCache().getIfPresent(channel) == null) {
                channel.close();
            }
        });

        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        run();
    }
}
