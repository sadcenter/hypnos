package app.hypnos.client;

import app.hypnos.client.commands.KeepAliveThread;
import app.hypnos.client.commands.MessageThread;
import app.hypnos.client.commands.ShutdownThread;
import app.hypnos.client.connection.Connection;
import app.hypnos.client.utils.HardwareUtil;
import app.hypnos.network.packet.impl.client.ClientAuthenticatePacket;
import app.hypnos.network.packet.storage.PacketStorage;
import app.hypnos.utils.MessageUtil;
import app.hypnos.utils.logging.LogType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

@Getter
public final class Client {

    public static final Thread MAIN_THREAD = Thread.currentThread();
    private static final int VERSION = 31;
    public static Client INSTANCE;
    private PacketStorage packetStorage;
    private Connection connection;
    private MessageThread messageThread;

    @SneakyThrows
    public Client() {
        INSTANCE = this;
        MessageUtil.clear();
        Scanner scanner = new Scanner(System.in);
        String ip;
        int port;
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder().GET().uri(URI.create("http://95.214.52.221:4893/api")).build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            MessageUtil.sendMessage("Can't connect to api.", Ansi.Color.RED, LogType.ERROR, true);
            System.exit(-1);
            return;
        }
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        if (jsonObject.get("version").getAsInt() != VERSION) {
            MessageUtil.sendMessage("Install new client version!", Ansi.Color.CYAN, LogType.INFO, true);
            System.exit(-1);
            return;
        }
        ip = jsonObject.get("ip").getAsString();
        port = jsonObject.get("port").getAsInt();
        MessageUtil.sendMessage("Username: ", Ansi.Color.CYAN, LogType.INFO, false);
        String userName = scanner.nextLine();
        MessageUtil.sendMessage("Password: ", Ansi.Color.CYAN, LogType.INFO, false);
        String password = scanner.nextLine();
        packetStorage = new PacketStorage();
        MessageUtil.clear();
        connection = new Connection(ip, port);
        while (connection.getChannel() == null) {
            System.out.println();
        }
        authenticate(userName, password);
        initialize();
        new KeepAliveThread().start();
        MessageUtil.clear();
    }

    public void authenticate(String userName, String password) {
        connection.sendToServer(new ClientAuthenticatePacket(userName, password, HardwareUtil.generateHardwareHash()));
    }

    public void initialize() {
        messageThread = new MessageThread(this);
        messageThread.start();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }
}