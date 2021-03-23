package app.hypnos.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AccountType {

    ADMIN(-1),
    PREMIUM(3),
    CLIENT(2),
    TRIAL(0);

    private final int maxSnipes;

    public boolean can(AccountType accountType) {
        return this.ordinal() <= accountType.ordinal();
    }
}