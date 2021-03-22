package app.hypnos.network.codec;

import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.storage.PacketStorage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PacketCodec extends ByteToMessageCodec<Packet> {

    private final PacketStorage packetStorage;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        out.writeByte(packet.getId());
        packet.write(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            byte id = in.readByte();
            packetStorage.get(id).ifPresentOrElse(packet -> {
                packet.read(in);
                if (in.isReadable()) {
                    //       throw new DecoderException("Packet still readable! (" + packet.getClass().getSimpleName() + " / " + in.readableBytes() + " extra bytes)");
                }
                out.add(packet);
            }, () -> {
                throw new DecoderException("Unknown packet with id: " + id);
            });
        } catch (Exception exception) {
            in.skipBytes(in.readableBytes());
            ctx.pipeline().remove(this);
            ctx.fireExceptionCaught(exception);
        }
    }
}