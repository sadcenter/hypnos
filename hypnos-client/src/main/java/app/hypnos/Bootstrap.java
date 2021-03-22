package app.hypnos;

import app.hypnos.client.Client;
import com.sun.jna.Platform;
import org.fusesource.jansi.AnsiConsole;

public final class Bootstrap {

    public static void main(String[] args) {
        if (!Platform.isWindows()) {
            System.out.println("Sorry :( We don't support other os than Window");
            return;
        }

        AnsiConsole.systemInstall();
        new Client();
    }

}
