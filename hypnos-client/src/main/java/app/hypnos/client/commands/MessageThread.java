package app.hypnos.client.commands;

import app.hypnos.client.Client;
import app.hypnos.network.packet.impl.client.ClientCommandPacket;
import app.hypnos.utils.MessageUtil;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

import java.util.Scanner;

public final class MessageThread extends Thread {

    private final Scanner scanner = new Scanner(System.in);
    private final Client client;

    public MessageThread(Client client) {
        this.client = client;

        setDaemon(true);
        setName("hypnos.app - messaging thread #01");
    }

    @Override
    public void run() {
        while (scanner.hasNextLine()) {
            String commandText = scanner.nextLine();

            if (commandText.isEmpty()) {
                MessageUtil.sendMessage("Write a command, not empty text.", Ansi.Color.RED, LogType.ERROR, true);
                stop();
                start();
                return;
            }

            client.getConnection().sendToServer(new ClientCommandPacket(commandText));

        }
    }
}
