package app.hypnos.server.connection.codec;

import app.hypnos.network.packet.Packet;
import app.hypnos.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequiredArgsConstructor
public class ServerPacketCodec extends ByteToMessageCodec<Packet> {

    private final Logger logger = LoggerFactory.getLogger(ServerPacketCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        out.writeByte(packet.getId());
        packet.write(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            byte id = in.readByte();
            Server.INSTANCE.getPacketStorage().get(id).ifPresentOrElse(packet -> {
                packet.read(in);
                if (in.isReadable()) {
                    logger.warn("Packet overload detected! (" + packet.getClass().getSimpleName() + " / " + in.readableBytes() + " extra bytes)");
                    //     logger.warn("For safety, blocking address for 3 days!");
                    //      Server.INSTANCE.getBlockedAddresses().put(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress(), System.currentTimeMillis());
                }
                out.add(packet);
            }, () -> {
                throw new DecoderException("Unknown packet with id: " + id);
            });
        } catch (Exception exception) {
            in.skipBytes(in.readableBytes());
            ctx.fireExceptionCaught(exception);
        }
    }
}