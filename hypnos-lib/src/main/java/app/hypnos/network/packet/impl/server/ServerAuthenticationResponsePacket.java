package app.hypnos.network.packet.impl.server;

import app.hypnos.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerAuthenticationResponsePacket extends Packet {

    private boolean successful;
    private String additionalInformation;

    {
        super.setId((byte) 5);
    }

    @Override
    public void read(ByteBuf buf) {
        successful = super.readBoolean(buf);
        additionalInformation = super.readString(Short.MAX_VALUE, buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBoolean(successful);
        super.writeString(buf, additionalInformation);
    }
}