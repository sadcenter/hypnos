package app.hypnos.network.packet.impl.client;

import app.hypnos.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class ClientKeepAlivePacket extends Packet {

    {
        super.setId((byte) 3);
    }

    @Override
    public void read(ByteBuf buf) {

    }

    @Override
    public void write(ByteBuf buf) {

    }
}
