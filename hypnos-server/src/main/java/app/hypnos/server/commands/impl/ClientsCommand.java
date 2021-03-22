package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.DateUtil;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

import java.util.Set;

public final class ClientsCommand extends Command {

    public ClientsCommand() {
        super("clients", AccountType.ADMIN, "client", "klienci");
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        Set<User> connectedUsers = Server.INSTANCE.getConnectedUsers();
        user.sendMessage("Online clients: " + connectedUsers.size(), Ansi.Color.GREEN, LogType.INFO);
        connectedUsers.forEach(connected -> {
            user.sendMessage(" --> " + connected.getUserName() + " " + DateUtil.timeToString(System.currentTimeMillis() - connected.getConnectedSince()),
                    Ansi.Color.MAGENTA, LogType.INFO);
        });
    }
}
