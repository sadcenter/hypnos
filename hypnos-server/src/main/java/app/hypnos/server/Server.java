package app.hypnos.server;

import app.hypnos.network.packet.storage.PacketStorage;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.impl.*;
import app.hypnos.server.connection.Connection;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.server.database.ConverterCodec;
import app.hypnos.server.database.impl.UserConverterCodec;
import app.hypnos.server.threads.KeepAliveThread;
import app.hypnos.server.threads.SaveDataThread;
import app.hypnos.server.utils.AuthUtil;
import app.hypnos.type.AccountType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.netty.channel.Channel;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class Server {

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static Server INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private final Set<User> users = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Command> commands = Sets.newHashSet(new UnBanCommand(), new SnipeCommand(), new  SnipesCommand(),  new KickClientCommand(), new BanClientCommand(), new ClientsCommand(), new StatsCommand());

    private final Cache<Channel, Object> keepAliveCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build();

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final PacketStorage packetStorage = new PacketStorage();

    private MongoDatabase mongoDatabase;


    public Server() {
        if (INSTANCE != null) {
            logger.info("Can't create another Server instance!");
            return;
        }

        INSTANCE = this;

        startMongo(new UserConverterCodec());

       // User user = new User("daniulek", AuthUtil.generateAuthToken("daniulek", "daniulek"),
         //       AccountType.CLIENT,
           //     new HashSet<>(),
             //   new HashSet<>());

     //        this.mongoDatabase.getCollection("users", User.class).insertOne(user);

        loadDatabase();

        executorService.execute(() -> new Connection(5482));

        new SaveDataThread(this).start();
        new KeepAliveThread(this).start();
    }


    private void loadDatabase() {
        mongoDatabase.getCollection("users", User.class).find().forEach(users::add);
        logger.info("Loaded " + users.size() + " users from database!");
    }

    private void startMongo(ConverterCodec<?>... converters) {
        CodecRegistry codecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        codecRegistry = CodecRegistries.fromRegistries(
                codecRegistry,
                CodecRegistries.fromCodecs(
                        converters
                )
        );
        mongoDatabase = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://root:Kacper123@95.214.52.221:27017/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false"))
                        .codecRegistry(codecRegistry)
                        .build()
        ).getDatabase("hypnos");
    }


    public User findByChannel(Channel channel) {
        return users.stream().filter(user -> user.getChannel() != null && user.getChannel().remoteAddress().equals(channel.remoteAddress())).findFirst().orElse(null);
    }

    public Optional<User> findByToken(String token) {
        return users.stream().filter(user -> user.getAuthToken().equals(token)).findFirst();
    }

    public Optional<User> findByName(String name) {
        return users.stream().filter(user -> user.getUserName().equals(name)).findFirst();
    }

    public Snipe findSnipe(String name) {
        return users.stream().map(user -> user.getSnipe(name)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public Set<User> getConnectedUsers() {
        return users.stream().filter(User::isOnline).collect(Collectors.toSet());
    }
}