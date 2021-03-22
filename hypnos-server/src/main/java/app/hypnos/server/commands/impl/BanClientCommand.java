package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.Ban;
import app.hypnos.server.data.User;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;

public final class BanClientCommand extends Command {

    public BanClientCommand() {
        super("ban", AccountType.ADMIN);
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("Correct usage: ban [user] [reason]");
        }

        String reason = args.length >= 2 ? StringUtils.join(args, ' ', 1, args.length) : "Not provided";

        Server.INSTANCE.findByName(args[0]).ifPresentOrElse(toBan -> {
            toBan.setBan(new Ban(reason, user.getUserName()));
            toBan.getSnipes().clear();
            if (toBan.isOnline()) {
                toBan.getChannel().close();
            }
            toBan.setUpdateRequired(true);
            user.sendMessage("User banned!", Ansi.Color.GREEN, LogType.INFO);
        }, () -> user.sendMessage("This user dont exists!", Ansi.Color.YELLOW, LogType.WARNING));
    }
}
