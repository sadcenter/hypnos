package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.User;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

public final class HelpCommand extends Command {

    public HelpCommand() {
        super("help", AccountType.TRIAL);
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        Server.INSTANCE.getCommands().forEach(command -> {
            if (command.getPermission().can(user.getAccountType())) {
                user.sendMessage(command.getName(), Ansi.Color.GREEN, LogType.INFO);
            }
        });
    }
}
