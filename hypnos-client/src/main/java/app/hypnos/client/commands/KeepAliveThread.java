package app.hypnos.client.commands;

import app.hypnos.client.Client;
import app.hypnos.network.packet.impl.client.ClientKeepAlivePacket;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;

public class KeepAliveThread extends AbstractScheduledService {

    @Override
    protected void runOneIteration() {
        Client.INSTANCE.getConnection().sendToServer(new ClientKeepAlivePacket());
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0L, 3L, TimeUnit.SECONDS);
    }
}