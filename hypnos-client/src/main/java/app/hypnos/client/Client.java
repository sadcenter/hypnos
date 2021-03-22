package app.hypnos.client;

import app.hypnos.client.commands.KeepAliveThread;
import app.hypnos.client.commands.MessageThread;
import app.hypnos.client.connection.Connection;
import app.hypnos.client.utils.HardwareUtil;
import app.hypnos.network.packet.impl.client.ClientAuthenticatePacket;
import app.hypnos.network.packet.storage.PacketStorage;
import app.hypnos.utils.MessageUtil;
import app.hypnos.utils.logging.LogType;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;

import java.util.Scanner;

@Getter
public final class Client {

    public static final Thread MAIN_THREAD = Thread.currentThread();
    public static Client INSTANCE;
    private final PacketStorage packetStorage;
    private final Connection connection;

    @SneakyThrows
    public Client() {
        INSTANCE = this;
        //shit ass code, its "example"

        MessageUtil.clear();

        Scanner scanner = new Scanner(System.in);

        MessageUtil.sendMessage("Username: ", Ansi.Color.CYAN, LogType.INFO, false);
        String userName = scanner.nextLine();

        MessageUtil.sendMessage("Password: ", Ansi.Color.CYAN, LogType.INFO, false);
        String password = scanner.nextLine();

        packetStorage = new PacketStorage();

        MessageUtil.clear();

        connection = new Connection("127.0.0.1", 5482);

        while (connection.getChannel() == null) {

        }


        authenticate(userName, password);

        new KeepAliveThread(this).start();

        MessageUtil.clear();

        while (connection.getChannel().isOpen()) {

        }

        shutdown();


    }


    public void authenticate(String userName, String password) {
        System.out.println("sending auth");
        connection.getChannel().writeAndFlush(new ClientAuthenticatePacket(userName, password, HardwareUtil.generateHardwareHash()));
    }

    @SneakyThrows
    public void initialize() {
        new MessageThread(this).start();

        //  Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
    }

    public void shutdown() {
        Channel server = connection == null ? null : connection.getChannel();
        if (server != null && server.isOpen()) {
            server.close();
        }
        System.exit(0);
    }
}