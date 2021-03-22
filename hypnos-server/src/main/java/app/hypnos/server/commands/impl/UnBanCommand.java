package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.User;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

public final class UnBanCommand extends Command {

    public UnBanCommand() {
        super("unban", AccountType.ADMIN);
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("Correct usage: unban [nick]");
        }

        Server.INSTANCE.findByName(args[0]).ifPresentOrElse(bannedUser -> {
            if (!bannedUser.isBanned()) {
                user.sendMessage("This user isn't banned", Ansi.Color.YELLOW, LogType.WARNING);
                return;
            }

            bannedUser.setBan(null);
        }, () -> user.sendMessage("Null user", Ansi.Color.YELLOW, LogType.WARNING));

    }
}
