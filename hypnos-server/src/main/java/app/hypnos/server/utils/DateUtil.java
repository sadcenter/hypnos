package app.hypnos.server.utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateUtil {

    public static String timeToString(long time) {
        if (time < 1L) {
            return "< 1s";
        }

        long[] units = {TimeUnit.MILLISECONDS.toDays(time) / 30L, TimeUnit.MILLISECONDS.toDays(time) % 30L, TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(time)), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))};
        StringBuilder stringBuilder = new StringBuilder();

        if (units[0] > 0L) {
            stringBuilder.append(units[0]).append("mo")
                    .append(" ");
        }
        if (units[1] > 0L) {
            stringBuilder.append(units[1]).append("d")
                    .append(" ");
        }
        if (units[2] > 0L) {
            stringBuilder.append(units[2]).append("h")
                    .append(" ");
        }
        if (units[3] > 0L) {
            stringBuilder.append(units[3]).append("m")
                    .append(" ");
        }
        if (units[4] > 0L) {
            stringBuilder.append(units[4]).append("s");
        }

        return stringBuilder.length() > 0 ? stringBuilder.toString().trim() : time + "ms";
    }

    public static @NotNull String getDate(final long time) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

}
