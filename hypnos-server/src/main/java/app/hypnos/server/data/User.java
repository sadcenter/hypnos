package app.hypnos.server.data;

import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.impl.server.ServerMessagePacket;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import org.bson.Document;
import org.fusesource.jansi.Ansi;

import java.util.Set;

@Data
public final class User {

    private final String userName;
    private final String authToken;
    private final AccountType accountType;
    private final Set<Snipe> snipes;
    private final Set<String> logs;
    private String hardwareIdentifier;
    private Ban ban;
    private int successSnipes;

    private transient boolean updateRequired;
    private transient long connectedSince;

    private transient Channel channel;

    public void sendMessage(String message, Ansi.Color color, LogType logType) {
        if (channel == null) {
            return;
        }

        sendPacket(new ServerMessagePacket(message, color, logType));
    }

    public void sendPacket(Packet packet) {
        if (channel.isOpen()) {
            channel.writeAndFlush(packet)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public Snipe getSnipe(String name) {
        return snipes.stream()
                .filter(snipe -> snipe.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Document getQuery() {
        return new Document("userName", userName);
    }

    public boolean isOnline() {
        return channel != null;
    }

    public boolean isBanned() {
        return ban != null;
    }

}