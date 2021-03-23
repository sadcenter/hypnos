package app.hypnos.network.packet;

import io.netty.buffer.ByteBuf;
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
        short length = byteBuf.readShort();
        System.out.println(length);
        byte[] bytes = new byte[length];
        System.out.println(bytes.length);
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public String readString(int maxLength, ByteBuf byteBuf) {
        return new String(readByteArray(byteBuf), StandardCharsets.UTF_8);
    }

    public boolean readBoolean(ByteBuf byteBuf) {
        return byteBuf.readBoolean();
    }

    public void writeString(ByteBuf byteBuf, String string) {
        writeByteArray(byteBuf, string.getBytes(StandardCharsets.UTF_8));
    }
}