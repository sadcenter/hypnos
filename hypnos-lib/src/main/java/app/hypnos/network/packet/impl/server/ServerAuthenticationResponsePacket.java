package app.hypnos.network.packet.impl.server;

import app.hypnos.network.packet.Packet;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class ServerAuthenticationResponsePacket extends Packet {

    private boolean successful;
    private List<String> additionalInformation;
    private final Gson gson = new Gson();

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
        additionalInformation = gson.fromJson(super.readString(Short.MAX_VALUE, buf), ArrayList.class);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBoolean(successful);
        super.writeString(buf, gson.toJson(additionalInformation));
    }
}