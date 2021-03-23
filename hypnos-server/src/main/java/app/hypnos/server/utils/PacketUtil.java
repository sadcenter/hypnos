package app.hypnos.server.utils;

import app.hypnos.network.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class PacketUtil {

    public static void close(Channel channel, Packet disconnectPacket) {
        sendPacket(channel, disconnectPacket, ChannelFutureListener.CLOSE);
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