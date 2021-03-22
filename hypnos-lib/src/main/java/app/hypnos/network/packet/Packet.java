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
        int length = byteBuf.readShort();
        int readerIndex = byteBuf.readerIndex();
        String string = byteBuf.toString(readerIndex, length, StandardCharsets.UTF_8);
        byteBuf.readerIndex(readerIndex + length);
        if (string.length() > maxLength) {
            System.out.println("d");
            throw new DecoderException("Readed string length is too long!");
        }
        return string;
    }

    public boolean readBoolean(ByteBuf byteBuf) {
        return byteBuf.readBoolean();
    }

    public void writeString(ByteBuf byteBuf, String string) {
        writeByteArray(byteBuf, string.getBytes(StandardCharsets.UTF_8));
    }
}