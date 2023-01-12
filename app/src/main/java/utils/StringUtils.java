package utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.demo.R;

import java.util.Formatter;
import java.util.Locale;

public class StringUtils {
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());



    public static String makeTimeString(@NonNull Context context, long secs) {
        sFormatBuilder.setLength(0);
        //return (secs < 0 ? "- " : "") + (Math.abs(secs) < 3600 ? makeShortTimeString(context, Math.abs(secs)) : makeLongTimeString(context, Math.abs(secs)));
        return Math.abs(secs) < 3600 ? makeShortTimeString(context, secs) : makeLongTimeString(context, secs);
    }

    private static String makeLongTimeString(@NonNull Context context, long secs) {
        return makeTimeString(context.getString(R.string.durationformatlong), secs);
    }

    private static String makeShortTimeString(@NonNull Context context, long secs) {
        return makeTimeString(context.getString(R.string.durationformatshort), secs);
    }

    private static String makeTimeString(String formatString, long secs) {
        long absSeconds = Math.abs(secs);
        sFormatBuilder.setLength(0);
        return sFormatter.format(formatString,
                secs < 0 ? "- " : "",
                absSeconds / 3600,
                absSeconds / 60,
                absSeconds / 60 % 60,
                absSeconds,
                absSeconds % 60)
                .toString();
    }

    public static int parseInt(@Nullable String string) {
        if (string != null) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException ignored) {

            }
        }
        return -1;
    }

}
