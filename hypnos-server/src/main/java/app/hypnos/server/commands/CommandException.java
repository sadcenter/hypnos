package app.hypnos.server.commands;

public class CommandException extends Exception {

    private final String message;

    public CommandException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
