package org.foxesworld.utils.helper;

import org.fusesource.jansi.Ansi;
import org.foxesworld.utils.Version;

/*
 * Nashorn при инициализации LogHelper пытается инициализировтаь все доступные в нем методы.
 * При попытке инициализировать rawAnsiFormat он пытается найти класс org.fusesource.jansi.Ansi
 * И есстественно крашится с ClassNotFound
 * В итоге любой вызов LogHelper.* приводит к ClassNotFound org.fusesource.jansi.Ansi
 * Поэтому rawAnsiFormat вынесен в отдельный Helper
 */
public class FormatHelper {
    public static Ansi rawAnsiFormat(LogHelper.Level level, String dateTime, boolean sub) {
        Ansi.Color levelColor;
        boolean bright = level != LogHelper.Level.DEBUG;
        switch (level) {
            case WARNING:
                levelColor = Ansi.Color.YELLOW;
                break;
            case ERROR:
                levelColor = Ansi.Color.RED;
                break;
            default: // INFO, DEBUG, Unknown
                levelColor = Ansi.Color.WHITE;
                break;
        }

        // Date-time
        Ansi ansi = new Ansi();
        ansi.fg(Ansi.Color.WHITE).a(dateTime);

        // Level
        ansi.fgBright(Ansi.Color.WHITE).a(" [").bold();
        if (bright) {
            ansi.fgBright(levelColor);
        } else {
            ansi.fg(levelColor);
        }
        ansi.a(level).boldOff().fgBright(Ansi.Color.WHITE).a("] ");

        // Message
        if (bright) {
            ansi.fgBright(levelColor);
        } else {
            ansi.fg(levelColor);
        }
        if (sub) {
            ansi.a(' ').a(Ansi.Attribute.ITALIC);
        }

        // Finish with reset code
        return ansi;
    }

    public static String ansiFormatVersion(String product) {
        return new Ansi().bold().fgRgb(255, 92, 17).a("FoxesWorld ").
                fgRgb(72, 209, 204).a(product). // Product
                fgBright(Ansi.Color.WHITE).a(" v").fgBright(Ansi.Color.BLUE).a(Version.getVersion().toString()). // Version
                fgBright(Ansi.Color.WHITE).a(" (build #").fgBright(Ansi.Color.RED).a(Version.getVersion().build).fgBright(Ansi.Color.WHITE).a(')'). // Build#
                reset().toString(); // To file
    }

    public static String ansiFormatLicense(String product) {
        return new Ansi().bold(). // Setup
                fgRgb(255, 92, 17).a("License for ").
                fgRgb(72, 209, 204).a(product). // Product
                fgBright(Ansi.Color.WHITE).a(" GNU GPLv3").fgBright(Ansi.Color.WHITE).a(". SourceCode: "). // Version
                fgBright(Ansi.Color.YELLOW).a("https://github.com/FoxesWorld/GradleLauncher").
                reset().toString(); // To file
    }

    public static String rawFormat(LogHelper.Level level, String dateTime, boolean sub) {
        return dateTime + " [" + level.name + (sub ? "]  " : "] ");
    }

    public static String formatVersion(String product) {
        return String.format("FoxesWorld %s v%s", product, Version.getVersion());
    }

    public static String formatLicense(String product) {
        return String.format("License for %s GNU GPLv3. SourceCode: https://github.com/FoxesWorld/GradleLauncher", product);
    }
}
