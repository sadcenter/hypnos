package app.hypnos.client.commands;

import app.hypnos.client.Client;
import app.hypnos.client.connection.Connection;
import app.hypnos.utils.MessageUtil;
import org.fusesource.jansi.Ansi;

public final class ShutdownThread extends Thread {

    public ShutdownThread() {
        super.setDaemon(true);
    }

    @Override
    public void run() {
        MessageUtil.sendMessage("Shutting down...", Ansi.Color.CYAN);
        Connection connection = Client.INSTANCE.getConnection();
        if (connection != null) {
            connection.close();
        }

        if (Client.INSTANCE.getMessageThread() != null) {
            Client.INSTANCE.getMessageThread().interrupt();
        }
    }
}