package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.User;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;

public class SaveDataThread extends AbstractScheduledService {

    @Override
    protected void runOneIteration() {
        Server.INSTANCE.getUsers().forEach(user -> {
            if (user.isUpdateRequired()) {
                Server.INSTANCE.getMongoDatabase().getCollection("users", User.class).replaceOne(user.getQuery(), user);
            }
        });
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0L, 60L, TimeUnit.SECONDS);
    }
}