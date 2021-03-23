package app.hypnos.client.commands;

import app.hypnos.client.Client;
import app.hypnos.network.packet.impl.client.ClientKeepAlivePacket;

public class KeepAliveThread extends Thread {

    public KeepAliveThread() {
        super.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            if (Client.INSTANCE.getConnection().getChannel().isOpen()) {
                Client.INSTANCE.getConnection().sendToServer(new ClientKeepAlivePacket());
            } else {
                System.exit(-1);
            }

            Thread.sleep(3000L);
        } catch (Exception exception) {
            System.exit(-1);
        }

        run();
    }
}
