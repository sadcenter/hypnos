package app.hypnos.client.connection.handler;

import app.hypnos.client.Client;
import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.impl.server.ServerAuthenticationResponsePacket;
import app.hypnos.network.packet.impl.server.ServerDisconnectPacket;
import app.hypnos.network.packet.impl.server.ServerMessagePacket;
import app.hypnos.utils.MessageUtil;
import app.hypnos.utils.logging.LogType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.fusesource.jansi.Ansi;

import java.util.ArrayDeque;

public class GlobalPacketHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Client.INSTANCE.initialize();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if (packet instanceof ServerMessagePacket serverMessagePacket) {
            MessageUtil.sendMessage(serverMessagePacket.getMessage(), serverMessagePacket.getAnsiColor(), serverMessagePacket.getLogType(), true);
        } else if (packet instanceof ServerDisconnectPacket serverDisconnectPacket) {
            MessageUtil.sendMessage(serverDisconnectPacket.getReason(), serverDisconnectPacket.getAnsiColor(), serverDisconnectPacket.getLogType(), true);
        } else if (packet instanceof ServerAuthenticationResponsePacket serverAuthenticationResponsePacket) {
            String additional = serverAuthenticationResponsePacket.getAdditionalInformation();
            if (serverAuthenticationResponsePacket.isSuccessful()) {
                MessageUtil.sendMessage(additional, Ansi.Color.GREEN, LogType.INFO, true);
            } else {
                MessageUtil.sendMessage(additional, Ansi.Color.RED, LogType.ERROR, true);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Client.INSTANCE.shutdown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Client.INSTANCE.shutdown();
    }
}