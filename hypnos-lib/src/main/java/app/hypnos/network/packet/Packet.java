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

    public void writeByteArray(ByteBuf byteBuf, byte[] array) {
        byteBuf.writeShort(array.length);
        byteBuf.writeBytes(array);
    }

    public byte[] readByteArray(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readShort()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public String readString(int maxLength, ByteBuf byteBuf) {
        String string = new String(readByteArray(byteBuf), StandardCharsets.UTF_8);
        int length = string.length();
        if (length > maxLength) {
            throw new DecoderException("String is longer than i expected! ("+length+" > "+maxLength+")");
        }
        return string;
    }

    public void writeString(ByteBuf byteBuf, String string) {
        writeByteArray(byteBuf, string.getBytes(StandardCharsets.UTF_8));
    }
}