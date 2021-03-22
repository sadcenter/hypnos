package app.hypnos.network.packet.impl.client;

import app.hypnos.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientCommandPacket extends Packet {

    private String command;

    {
        super.setId((byte) 1);
    }

    @Override
    public void read(ByteBuf buf) {
        command = super.readString(300, buf);
    }

    @Override
    public void write(ByteBuf buf) {
        super.writeString(buf, command);
    }
}