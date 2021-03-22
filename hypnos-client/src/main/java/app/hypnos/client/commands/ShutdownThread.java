package app.hypnos.client.commands;

import app.hypnos.client.Client;
import app.hypnos.utils.MessageUtil;
import io.netty.channel.Channel;
import org.fusesource.jansi.Ansi;

public final class ShutdownThread extends Thread {

    private final Client client;

    public ShutdownThread(Client client) {
        this.client = client;

        this.setDaemon(true);
    }

    @Override
    public void run() {
        MessageUtil.sendMessage("Shutting down...", Ansi.Color.CYAN);
        Channel server = client.getConnection() == null ? null : client.getConnection().getChannel();
        if (server != null && server.isOpen()) {
            server.close();
        }

        if (client.getMessageThread() != null) {
            client.getMessageThread().interrupt(); // Close the Thread
        }
    }
}