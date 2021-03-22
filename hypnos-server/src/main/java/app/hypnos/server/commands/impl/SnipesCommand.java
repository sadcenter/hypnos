package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.DateUtil;
import app.hypnos.server.utils.SniperUtil;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

import java.util.Set;

public final class SnipesCommand extends Command {

    public SnipesCommand() {
        super("snipes", AccountType.ADMIN);
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        Server.INSTANCE.getUsers().forEach(target -> {

            Set<Snipe> snipes = target.getSnipes();

            if (snipes.isEmpty()) {
                return;
            }

            user.sendMessage("User " + target.getUserName(), Ansi.Color.GREEN, LogType.INFO);
            snipes.forEach(snipe -> user.sendMessage(" ---> Sniping " + snipe.getName() + " (scheduled at "
                                                 + DateUtil.getDate(snipe.getAccessTime())
                                                 + ", left: " + DateUtil.timeToString(snipe.getAccessTime() - System.currentTimeMillis()) + ") views: "
                            + SniperUtil.getViews(snipe.getName()),
                    Ansi.Color.MAGENTA, LogType.INFO));

        });
    }
}
