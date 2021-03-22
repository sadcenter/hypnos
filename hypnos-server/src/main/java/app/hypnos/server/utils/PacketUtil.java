package app.hypnos.server.utils;

import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.impl.server.ServerDisconnectPacket;
import app.hypnos.utils.logging.LogType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.fusesource.jansi.Ansi;

public class PacketUtil {

    public static void close(Channel channel, String reason, Ansi.Color color) {
        if (channel.isOpen()) {
            channel.writeAndFlush(new ServerDisconnectPacket(reason, color, LogType.DISCONNECTED))
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void sendPacket(Channel channel, Packet packet) {
        if (channel.isOpen()) {
            channel.writeAndFlush(packet)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public static void sendPacket(Channel channel, Packet packet, ChannelFutureListener futureListener) {
        if (channel.isOpen()) {
            channel.writeAndFlush(packet)
                    .addListener(futureListener);
        }
    }
}