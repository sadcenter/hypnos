package app.hypnos.server.commands;

import app.hypnos.server.data.User;
import app.hypnos.type.AccountType;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;

@Getter
public abstract class Command {

    private final String name;
    private final AccountType permission;
    private final Set<String> aliases;

    protected Command(String name, AccountType permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = Sets.newHashSet(aliases);
    }

    public abstract void execute(User user, String... args) throws CommandException;

}
