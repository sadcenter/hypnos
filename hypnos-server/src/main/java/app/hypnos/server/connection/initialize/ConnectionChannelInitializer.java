package app.hypnos.server.connection.initialize;

import app.hypnos.network.codec.PacketCodec;
import app.hypnos.server.Server;
import app.hypnos.server.handler.GlobalPacketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ServerChannel;

public class ConnectionChannelInitializer extends ChannelInitializer<ServerChannel> {

    @Override
    protected void initChannel(ServerChannel channel) {
        channel.pipeline()
                .addLast("codec", new PacketCodec(Server.INSTANCE.getPacketStorage()))
                .addLast("global_handler", new GlobalPacketHandler());
    }
}