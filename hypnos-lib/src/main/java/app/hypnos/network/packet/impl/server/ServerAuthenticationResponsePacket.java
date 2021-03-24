package app.hypnos.network.packet.impl.server;

import app.hypnos.network.packet.Packet;
import app.hypnos.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class ServerAuthenticationResponsePacket extends Packet {

    private boolean successful;
    private List<String> additionalInformation;

    {
        super.setId((byte) 3);
    }

    public ServerAuthenticationResponsePacket(boolean successful, String... additionalInformation) {
        this.successful = successful;
        this.additionalInformation = Arrays.asList(additionalInformation);
    }

    @Override
    public void read(ByteBuf buf) {
        successful = buf.readBoolean();
        additionalInformation = SerializationUtil.deserializeList(super.readByteArray(buf));
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBoolean(successful);
        super.writeByteArray(buf, SerializationUtil.serializeList(additionalInformation));
    }
}