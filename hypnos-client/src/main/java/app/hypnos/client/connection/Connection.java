package app.hypnos.client.connection;

import app.hypnos.client.Client;
import app.hypnos.client.connection.handler.GlobalPacketHandler;
import app.hypnos.client.utils.LazyLoadBase;
import app.hypnos.network.codec.PacketCodec;
import app.hypnos.network.packet.Packet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executors;

@Getter
@Setter
public class Connection {

    private final LazyLoadBase<NioEventLoopGroup> nioEventLoopGroup = new LazyLoadBase<>() {
        @Override
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(new ThreadFactoryBuilder().setDaemon(true)
                    .build());
        }
    };

    private Channel channel;

    public Connection(String address, int port) {
        Executors.newCachedThreadPool().execute(() -> {
            try {
                ChannelFuture future = new Bootstrap()
                        .group(nioEventLoopGroup.getValue())
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.IP_TOS, 0x18)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                socketChannel.pipeline()
                                        .addLast("codec", new PacketCodec(Client.INSTANCE.getPacketStorage()))
                                        .addLast("global_handler", new GlobalPacketHandler());
                            }
                        })
                        .connect(address, port)
                        .sync();

                channel = future.channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                System.exit(0);
            }
        });
    }

    public void sendToServer(Packet packet) {
        if (channel.isOpen()) {
            channel.writeAndFlush(packet)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }
}