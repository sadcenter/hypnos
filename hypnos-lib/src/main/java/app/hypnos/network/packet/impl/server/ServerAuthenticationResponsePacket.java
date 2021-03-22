package app.hypnos.network.packet.impl.server;

import app.hypnos.network.packet.Packet;
import app.hypnos.type.AuthResponseType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerAuthenticationResponsePacket extends Packet {

    private AuthResponseType responseType;
    private String additionalInformation;

    {
        super.setId((byte) 5);
    }

    @Override
    public void read(ByteBuf buf) {
        responseType = AuthResponseType.values()[responseType.ordinal()];
        additionalInformation = super.readString(500, buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(responseType.ordinal());
        super.writeString(buf, additionalInformation);
    }
}