package app.hypnos.network.packet.storage;

import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.impl.client.ClientAuthenticatePacket;
import app.hypnos.network.packet.impl.client.ClientCommandPacket;
import app.hypnos.network.packet.impl.client.ClientKeepAlivePacket;
import app.hypnos.network.packet.impl.server.ServerAuthenticationResponsePacket;
import app.hypnos.network.packet.impl.server.ServerDisconnectPacket;
import app.hypnos.network.packet.impl.server.ServerMessagePacket;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PacketStorage {

    private final Map<Byte, Packet> packets = new ConcurrentHashMap<>();

    public PacketStorage() {
        register(new ClientAuthenticatePacket(),
                new ClientCommandPacket(),
                new ServerMessagePacket(),
                new ClientKeepAlivePacket(),
                new ServerDisconnectPacket(),
                new ServerAuthenticationResponsePacket());
    }

    private void register(Packet... packets) {
        for (Packet packet : packets) {
            this.packets.put(packet.getId(), packet);
        }
    }

    public Optional<Packet> get(byte id) {
        return Optional.of(packets.get(id));
    }
}