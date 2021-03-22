package app.hypnos.utils;

import app.hypnos.utils.logging.LogType;
import org.fusesource.jansi.Ansi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class MessageUtil {

    private static final DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

    public static void sendMessage(String message, Ansi.Color color) {
        sendMessage(message, color, LogType.INFO, true);
    }

    public static void sendMessage(String message, Ansi.Color color, LogType logType, boolean newLine) {
        Ansi reset = Ansi.ansi().fg(color).a("[" + dateFormatter.format(new Date()) + "] [" + logType.name().toUpperCase() + "] ").a(message).reset();
        if (newLine) {
            System.out.println(reset);
        } else {
            System.out.print(reset);
        }
    }


    public static void clear() {
        System.out.println(Ansi.ansi().eraseScreen().reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.MAGENTA).a("""
                 _                                     \s
                | |__   _   _  _ __   _ __    ___   ___\s
                | '_ \\ | | | || '_ \\ | '_ \\  / _ \\ / __|
                | | | || |_| || |_) || | | || (_) |\\__ \\
                |_| |_| \\__, || .__/ |_| |_| \\___/ |___/
                        |___/ |_|                      \s""".indent(38)).reset());
        for (int i = 0; i < 8; i++) {
            System.out.println(" ");
        }
    }

}
