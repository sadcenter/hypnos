package app.hypnos.utils.logging;

public enum LogType {

    ERROR("[ERROR]"),
    INFO("[INFO]"),
    WARNING("[WARNING]");

    private final String prefix;

    LogType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
