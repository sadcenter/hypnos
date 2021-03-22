package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.SniperUtil;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;

public final class NickNameSniperThread extends AbstractScheduledService {

    private final Server server;

    public NickNameSniperThread(Server server) {
        this.server = server;
    }

    @Override
    protected void runOneIteration() throws Exception {
        for (User user : this.server.getUsers()) {
            for (Snipe snipe : user.getSnipes()) {
                String authToken = SniperUtil.getAuthToken(snipe.getAccount());
                if (snipe.getAccessTime() - 1800 <= System.currentTimeMillis()) {
                    for (int i = 0; i < 5; i++) {
                        SniperUtil.changeName(user, snipe, authToken);
                    }

                }
            }
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0L, 1, TimeUnit.MILLISECONDS);
    }
}
