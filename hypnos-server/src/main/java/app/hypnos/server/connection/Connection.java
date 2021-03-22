package app.hypnos.server.connection;

import app.hypnos.network.codec.PacketCodec;
import app.hypnos.server.Server;
import app.hypnos.server.connection.util.LazyLoadBase;
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
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public class Connection {

    private final LazyLoadBase<EpollEventLoopGroup> epollEventLoopGroup = new LazyLoadBase<>() {
        @Override
        protected EpollEventLoopGroup load() {
            return new EpollEventLoopGroup(new ThreadFactoryBuilder()
                    .setNameFormat("Netty Epoll Loop Group IO #%d")
                    .setDaemon(true)
                    .build());
        }
    };

    private final LazyLoadBase<NioEventLoopGroup> nioEventLoopGroup = new LazyLoadBase<>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(new ThreadFactoryBuilder()
                    .setNameFormat("Netty Nio Loop Group IO #%d")
                    .setDaemon(true)
                    .build());
        }
    };

    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    @SneakyThrows
    public Connection(int port) {
        boolean epollAvailable = Epoll.isAvailable();
        EventLoopGroup group;
        Class<? extends ServerChannel> serverChannel;
        if (epollAvailable) {
            group = epollEventLoopGroup.getValue();
            serverChannel = EpollServerSocketChannel.class;
            logger.info("Using epoll group");
        } else {
            group = nioEventLoopGroup.getValue();
            serverChannel = NioServerSocketChannel.class;
            logger.info("Using nio group");
        }
        new ServerBootstrap()
                .group(group)
                .channel(serverChannel)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("codec", new PacketCodec(Server.INSTANCE.getPacketStorage()))
                                .addLast("global_handler", new GlobalPacketHandler());
                    }
                })
                .bind(port)
                .sync()
                .channel()
                .closeFuture()
                .sync();
    }
}