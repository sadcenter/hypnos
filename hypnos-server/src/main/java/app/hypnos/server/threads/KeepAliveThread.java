package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.User;
import com.google.common.util.concurrent.AbstractScheduledService;
import io.netty.channel.ChannelOutboundInvoker;

import java.util.concurrent.TimeUnit;

public class KeepAliveThread extends AbstractScheduledService {

    @Override
    protected void runOneIteration() {
        Server.INSTANCE.getConnectedUsers().stream().map(User::getChannel)
                .filter(channel -> Server.INSTANCE.getKeepAliveCache().getIfPresent(channel) == null)
                .forEach(ChannelOutboundInvoker::close);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0L, 1_500L, TimeUnit.MILLISECONDS);
    }
}