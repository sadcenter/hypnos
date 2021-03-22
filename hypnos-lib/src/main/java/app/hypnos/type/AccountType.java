package app.hypnos.type;

public enum AccountType {

    ADMIN(-1),
    PREMIUM(3),
    CLIENT(2),
    TRIAL(0);

    private final int maxSnipes;

    AccountType(int maxSnipes) {
        this.maxSnipes = maxSnipes;
    }

    public int getMaxSnipes() {
        return maxSnipes;
    }

    public boolean can(AccountType accountType) {
        return this.ordinal() <= accountType.ordinal();
    }
}