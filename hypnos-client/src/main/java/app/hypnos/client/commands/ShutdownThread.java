package app.hypnos.client.commands;

import app.hypnos.client.Client;

public final class ShutdownThread extends Thread {

    private final Client client;

    public ShutdownThread(Client client) {
        this.client = client;

        this.setDaemon(true);
    }

    @Override
    public void run() {
        client.shutdown();
    }
}
