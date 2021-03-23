package app.hypnos.server.connection;

import app.hypnos.server.Server;
import app.hypnos.server.connection.codec.ServerPacketCodec;
import app.hypnos.server.handler.GlobalPacketHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Connection {

    public Connection(int port) {
        EventLoopGroup group;
        Class<? extends ServerChannel> serverChannel;
        if (Epoll.isAvailable()) {
            group = new EpollEventLoopGroup(new ThreadFactoryBuilder()
                    .setNameFormat("Netty Epoll Loop Group IO #%d")
                    .setDaemon(true)
                    .build());
            serverChannel = EpollServerSocketChannel.class;
        } else {
            group = new NioEventLoopGroup(new ThreadFactoryBuilder()
                    .setNameFormat("Netty Nio Loop Group IO #%d")
                    .setDaemon(true)
                    .build());
            serverChannel = NioServerSocketChannel.class;
        }
        try {
            new ServerBootstrap()
                    .group(group)
                    .channel(serverChannel)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast("codec", new ServerPacketCodec(Server.INSTANCE.getPacketStorage()))
                                    .addLast("global_handler", new GlobalPacketHandler());
                        }
                    })
                    .bind(port)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}