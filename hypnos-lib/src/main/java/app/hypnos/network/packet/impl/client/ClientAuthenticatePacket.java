package app.hypnos.network.packet.impl.client;

import app.hypnos.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientAuthenticatePacket extends Packet {

    {
        super.setId((byte) 0);
    }

    private String name;
    private String pass;
    private String hash;

    @Override
    public void read(ByteBuf buf) {
        name = super.readString(16, buf);
        pass = super.readString(54, buf);
        hash = super.readString(100, buf);
    }

    @Override
    public void write(ByteBuf buf) {
        super.writeString(buf, name);
        super.writeString(buf, pass);
        super.writeString(buf, hash);
    }
}