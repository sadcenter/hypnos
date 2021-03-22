package app.hypnos.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fusesource.jansi.Ansi;

@RequiredArgsConstructor
@Getter
public enum AuthResponseType {

    SUCCESS(true, Ansi.Color.GREEN),
    ERROR(false, Ansi.Color.RED);

    private final boolean successful;
    private final Ansi.Color color;
}