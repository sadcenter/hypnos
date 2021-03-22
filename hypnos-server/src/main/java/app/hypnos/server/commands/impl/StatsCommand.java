package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.User;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

public final class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", AccountType.ADMIN);
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        user.sendMessage("Online clients: " + Server.INSTANCE.getConnectedUsers().size(), Ansi.Color.GREEN, LogType.INFO);
        user.sendMessage("Clients in database: " + Server.INSTANCE.getUsers().size(), Ansi.Color.GREEN, LogType.INFO);
    }
}
