package app.hypnos.client.connection.handler;

import app.hypnos.client.Client;
import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.impl.server.ServerMessagePacket;
import app.hypnos.utils.MessageUtil;
import app.hypnos.utils.logging.LogType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.fusesource.jansi.Ansi;

public class GlobalPacketHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Client.INSTANCE.initialize();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if (packet instanceof ServerMessagePacket serverMessagePacket) {
            MessageUtil.sendMessage(serverMessagePacket.getMessage(), serverMessagePacket.getAnsiColor(), serverMessagePacket.getLogType(), true);
            if(serverMessagePacket.getMessage().contains("Logged in"))
                System.out.println(" "); // ale gowno jebane XDD
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MessageUtil.sendMessage("Bye!", Ansi.Color.RED, LogType.ERROR, true);
        Client.INSTANCE.shutdown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        MessageUtil.sendMessage("Bye!", Ansi.Color.RED, LogType.ERROR, true);
        Client.INSTANCE.shutdown();
    }
}