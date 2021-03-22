package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.User;

public final class SaveDataThread extends Thread {

    private final Server server;

    public SaveDataThread(Server server) {
        this.server = server;

        setDaemon(true);
        setName("save data thread");
    }

    @Override
    public void run() {
        server.getUsers().forEach(user -> {
            if (!user.isUpdateRequired()) {
                return;
            }

            server.getMongoDatabase().getCollection("users", User.class).replaceOne(user.getQuery(), user);
        });

        try {
            Thread.sleep(60_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        run();
    }
}
