package app.hypnos.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class Packet {

    private byte id;

    public abstract void read(ByteBuf buf);

    public abstract void write(ByteBuf buf);

    public void writeByteArray(ByteBuf buf, byte[] array) {
        buf.writeShort(array.length);
        buf.writeBytes(array);
    }

    public byte[] readByteArray(ByteBuf buf) {
        byte[] bytes = new byte[buf.readShort()];
        buf.readBytes(bytes);
        return bytes;
    }

    public String readString(int maxLength, ByteBuf buf) {
        short length = buf.readShort();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        String string = new String(bytes, StandardCharsets.UTF_8);
        if (string.length() > maxLength || string.length() != length) {
            throw new DecoderException("Unexpected string size!");
        }
        return string;
    }

    public void writeString(ByteBuf buf, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }
}