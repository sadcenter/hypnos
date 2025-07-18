package app.hypnos.server.handler;

import app.hypnos.network.packet.Packet;
import app.hypnos.network.packet.impl.client.ClientAuthenticatePacket;
import app.hypnos.network.packet.impl.client.ClientCommandPacket;
import app.hypnos.network.packet.impl.client.ClientKeepAlivePacket;
import app.hypnos.network.packet.impl.server.ServerAuthenticationResponsePacket;
import app.hypnos.network.packet.impl.server.ServerMessagePacket;
import app.hypnos.server.Server;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.Ban;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.AuthUtil;
import app.hypnos.server.utils.PacketUtil;
import app.hypnos.utils.logging.LogType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class GlobalPacketHandler extends SimpleChannelInboundHandler<Packet> {

    private final Logger logger = LoggerFactory.getLogger(GlobalPacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext handlerContext, Packet packet) {
        Channel channel = handlerContext.channel();
        User user = Server.INSTANCE.findByChannel(channel);
        if (packet instanceof ClientAuthenticatePacket && user != null) {
            channel.close();
            return;
        }
        if (packet instanceof ClientAuthenticatePacket clientAuthenticatePacket) {
            Server.INSTANCE.findByToken(AuthUtil.generateAuthToken(clientAuthenticatePacket.getName(), clientAuthenticatePacket.getPass())).ifPresentOrElse(byToken -> {
                if (byToken.isBanned()) {
                    Ban ban = byToken.getBan();
                    PacketUtil.sendPacket(channel, new ServerAuthenticationResponsePacket(false, "You are banned!",
                            " ---> Admin: "+ban.getAdmin(),
                            " ---> Reason: " + ban.getReason()),
                            ChannelFutureListener.CLOSE);
                    return;
                }

                if (byToken.getHardwareIdentifier() == null) {
                    byToken.setHardwareIdentifier(clientAuthenticatePacket.getHash());
                } else if (!byToken.getHardwareIdentifier().equals(clientAuthenticatePacket.getHash())) {
                    PacketUtil.sendPacket(channel, new ServerAuthenticationResponsePacket(false, "Invalid Hardware ID (Contact administrator)!"), ChannelFutureListener.CLOSE);
                    return;
                }
                if (byToken.getChannel() != null) {
                    PacketUtil.sendPacket(channel, new ServerAuthenticationResponsePacket(false, "User already logged " + clientAuthenticatePacket.getName()), ChannelFutureListener.CLOSE);
                    return;
                }

                byToken.setConnectedSince(System.currentTimeMillis());
                byToken.setChannel(channel);
                byToken.sendPacket(new ServerAuthenticationResponsePacket(true,
                        "Logged successful as " + byToken.getUserName(),
                        " ---> Account Type: " + byToken.getAccountType()));
                logger.info("User connected - " + byToken.getUserName() + " (" + byToken.getAccountType() + ")");
            }, () -> PacketUtil.sendPacket(channel, new ServerAuthenticationResponsePacket(false, "Invalid login data!"), ChannelFutureListener.CLOSE));
        } else if (packet instanceof ClientCommandPacket clientCommandPacket) {
            String[] split = clientCommandPacket.getCommand().split(" ");

            String commandName = split[0];
            Server.INSTANCE.getCommands().stream().filter(cmd -> cmd.getName().equals(commandName)
                    || cmd.getAliases().contains(commandName)).findFirst().ifPresentOrElse(command -> {
                if (!user.getAccountType().can(command.getPermission())) {
                    PacketUtil.sendPacket(channel, new ServerMessagePacket("You don't have access to " + command.getPermission() + " rank.", Ansi.Color.RED, LogType.INFO));
                    return;
                }
                try {
                    command.execute(user, Arrays.copyOfRange(split, 1, split.length));
                } catch (CommandException exception) {
                    user.sendMessage(exception.getMessage(), Ansi.Color.YELLOW, LogType.WARNING);
                }
            }, () -> PacketUtil.sendPacket(channel, new ServerMessagePacket("This command doesn't exists!", Ansi.Color.RED, LogType.INFO)));
        } else if (packet instanceof ClientKeepAlivePacket) {
            if (user == null) {
                if (channel.isOpen()) {
                    channel.close();
                }
                return;
            }

            Server.INSTANCE.getKeepAliveCache().put(channel, true);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext handlerContext, Throwable throwable) {
        handlerContext.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext handlerContext) {
        User user = Server.INSTANCE.findByChannel(handlerContext.channel());
        if (user != null) {
            if (user.isUpdateRequired()) {
                Server.INSTANCE.getMongoDatabase().getCollection("users", User.class).replaceOne(user.getQuery(), user);
            }
            user.setChannel(null);
            logger.info("User disconnected " + user.getUserName() + " (" + user.getAccountType() + ")");
        }
    }
}