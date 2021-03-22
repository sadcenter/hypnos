package app.hypnos.server.commands.impl;

import app.hypnos.server.Server;
import app.hypnos.server.commands.Command;
import app.hypnos.server.commands.CommandException;
import app.hypnos.server.data.Account;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.DateUtil;
import app.hypnos.server.utils.SniperUtil;
import app.hypnos.type.AccountType;
import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

import java.util.Locale;

public final class SnipeCommand extends Command {

    public SnipeCommand() {
        super("snipe", AccountType.CLIENT, "snajp");
    }

    @Override
    public void execute(User user, String... args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("Correct usage: snipe [start/stop/list] [nick] [username:password]");
        }

        String type = args[0];

        switch (type.toLowerCase(Locale.ROOT)) {
            case "start" -> {
                if (user.getAccountType().getMaxSnipes() > -1 && user.getSnipes().size() >= user.getAccountType().getMaxSnipes()) {
                    throw new CommandException("For you snipe limit is " + user.getAccountType().getMaxSnipes());
                }

                if (args.length < 4) {
                    throw new CommandException("Correct usage: snipe [start] [nick] [username:password] [old owner]");
                }

                String snipe = args[1];

                if (SniperUtil.getUniqueId(snipe) != null) {
                    throw new CommandException("This name is already taken.");
                }

                if (Server.INSTANCE.findSnipe(snipe) != null) {
                    throw new CommandException("This name is already sniping!");
                }

                String[] split = args[2].split(":");

                if (split.length != 2) {
                    throw new CommandException("Wrong username password format (correct format is \"combo\" (username:password))");
                }

                String oldOwner = args[3];

                String latestName = SniperUtil.getLatestName(oldOwner);

                if (latestName == null) {
                    throw new CommandException("Old owner is \"null\"!");
                }

                if (!latestName.equals(snipe)) {
                    throw new CommandException("You provided wrong old owner ;/");
                }


                Account account = new Account(split[0], split[1]);

                if (SniperUtil.getAuthToken(account) == null) {
                    throw new CommandException("You provided wrong username/password");
                }

                long accessTime = SniperUtil.getAccessTime(oldOwner);

                user.getSnipes().add(
                        new Snipe(snipe, account, accessTime)
                );
                user.setUpdateRequired(true);
                user.sendMessage("Started sniping " + snipe + ". Access time: "
                                + DateUtil.getDate(accessTime) + " (left: " + DateUtil.timeToString(accessTime - System.currentTimeMillis()) + ")",
                        Ansi.Color.GREEN, LogType.INFO);
            }
            case "list" -> {
                user.sendMessage("Printing your snipes...", Ansi.Color.CYAN, LogType.INFO);

                if (user.getSnipes().isEmpty()) {
                    user.sendMessage("Your snipes are empty!", Ansi.Color.YELLOW, LogType.INFO);
                } else {
                    user.getSnipes().forEach(snipe -> user.sendMessage("Sniping: " + snipe.getName() + " (scheduled at "
                                    + DateUtil.getDate(snipe.getAccessTime())
                                    + ", left: " + DateUtil.timeToString(snipe.getAccessTime() - System.currentTimeMillis()) + ") views " +
                                    SniperUtil.getViews(snipe.getName()),
                            Ansi.Color.GREEN,
                            LogType.INFO));
                }
            }
            case "stop" -> {
                if (args.length < 2) {
                    throw new CommandException("Correct usage: snipe stop [nick]");
                }

                Snipe snipe = user.getSnipe(args[1]);
                if (snipe == null) {
                    throw new CommandException("You dont sniping this nickname");
                }

                user.getSnipes().remove(snipe);
                user.sendMessage("Sniping stopped", Ansi.Color.GREEN, LogType.INFO);

            }
            default -> user.sendMessage("Wrong usage!", Ansi.Color.RED, LogType.ERROR);
        }
    }
}
