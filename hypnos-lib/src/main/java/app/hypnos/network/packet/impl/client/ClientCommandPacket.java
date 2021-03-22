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

    {
        super.setId((byte) 1);
    }

    private String command;

    @Override
    public void read(ByteBuf buf) {
        command = super.readString(60, buf);
    }

    @Override
    public void write(ByteBuf buf) {
        super.writeString(buf, command);
    }
}