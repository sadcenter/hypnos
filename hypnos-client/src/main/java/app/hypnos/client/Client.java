package app.hypnos.client;

import app.hypnos.client.commands.KeepAliveThread;
import app.hypnos.client.commands.MessageThread;
import app.hypnos.client.connection.Connection;
import app.hypnos.network.packet.storage.PacketStorage;
import app.hypnos.utils.MessageUtil;
import app.hypnos.utils.logging.LogType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public final class Client {

    public static final Thread MAIN_THREAD = Thread.currentThread();
    public static Client INSTANCE;

    @Setter
    private Connection connection;

    private final int VERSION = 31;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final PacketStorage packetStorage = new PacketStorage();
    private MessageThread messageThread;

    @SneakyThrows
    public Client() {
        INSTANCE = this;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        MessageUtil.clear();

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
            MessageUtil.sendMessage("Install new client version!", Ansi.Color.YELLOW, LogType.INFO, true);
            System.exit(-1);
            return;
        }

        ip = jsonObject.get("ip").getAsString();
        port = jsonObject.get("port").getAsInt();

        Scanner scanner = new Scanner(System.in);

        MessageUtil.sendMessage("Username: ", Ansi.Color.CYAN, LogType.INFO, false);
        String userName = scanner.next();

        MessageUtil.sendMessage("Password: ", Ansi.Color.CYAN, LogType.INFO, false);
        String password = scanner.next();

        MessageUtil.clear();

        connection = new Connection(ip, port, userName, password);
    }

    public void initialize() {
        messageThread = new MessageThread(this);
        messageThread.start();
        new KeepAliveThread().startAsync();
        MessageUtil.clear();
    }

    @SneakyThrows
    public void shutdown() {
        Channel server = connection == null ? null : connection.getChannel();
        if (server != null && server.isOpen()) {
            server.close();
        }

        if (messageThread != null) {
            messageThread.interrupt(); // Close the Thread
        }

        Thread.sleep(3000L);
        System.exit(0);
    }
}