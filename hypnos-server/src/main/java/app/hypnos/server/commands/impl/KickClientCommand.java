package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.User;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

import java.util.Optional;

public final class KickClientCommand extends Command {

    public KickClientCommand() {
        super("kick", AccountType.ADMIN);
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        if (args.length == 0)
            throw new CommandException("Corect usage: kick [userName]");

        Optional<User> optionalUser = Server.INSTANCE.findByName(args[0]);

        optionalUser.ifPresentOrElse(result -> {
            result.getChannel().close();
            user.sendMessage("Kicked " + result.getUserName() + " from session!", Ansi.Color.GREEN, LogType.INFO);
        }, () -> {
            user.sendMessage("Provided user dont exists!", Ansi.Color.YELLOW, LogType.WARNING);
        });
    }
}
