package app.hypnos.network.packet.impl.server;

import app.hypnos.network.packet.Packet;
import app.hypnos.utils.logging.LogType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fusesource.jansi.Ansi;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerMessagePacket extends Packet {

    {
        super.setId((byte) 2);
    }

    private String message;
    private Ansi.Color ansiColor;
    private LogType logType;

    @Override
    public void read(ByteBuf buf) {
        logType = LogType.values()[buf.readInt()];
        ansiColor = Ansi.Color.values()[buf.readInt()];
        message = super.readString(400, buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(logType.ordinal());
        buf.writeInt(ansiColor.ordinal());
        super.writeString(buf, message);
    }
}