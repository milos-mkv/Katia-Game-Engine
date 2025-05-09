package org.katia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Katia Logger utility class for logging data in formatted way into console.
 */
public abstract class Logger {

    public enum Type {
        INFO,
        WARNING,
        ERROR,
        SUCCESS,
        LUA,
        DISPOSE
    }

    static List<String> logs;
    static DateTimeFormatter formatter;
    static boolean enabled = true;
    /**
     * Initialize logger class.
     */
    public static void initialize() {
        logs = new ArrayList<>();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Disable logger.
     */
    public static void disable() {
        enabled = false;
    }

    /**
     * Enable logger.
     */
    public static void enable() {
        enabled = true;
    }

    /**
     * Log messages.
     * @param type Log type.
     * @param params Messages to log.
     */
    public static void log(Type type, String... params) {
        if (!enabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getDateTime()).append(getLogTypeText(type));
        for (String param : params) {
            stringBuilder.append(param).append(" ");
        }
        logs.add(stringBuilder.toString());
        System.out.println(stringBuilder);
    }

    /**
     * Log message (No date or log type).
     * @param params Log messages.
     */
    public static void log(String... params) {
        if (!enabled) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : params) {
            stringBuilder.append(param).append(" ");
        }
        logs.add(stringBuilder.toString());
        System.out.println(stringBuilder);
    }

    /**
     * Save all logs to file.
     * @param file Log file path.
     */
    public static void logToFile(String file) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String log : logs) {
            stringBuilder.append(log).append("\n");
        }
        try {
            Files.write(Paths.get(file), stringBuilder.toString().getBytes());
        } catch (IOException e) {
            log(Logger.Type.ERROR, "Failed to save logs into file:", file);
        }
    }

    /**
     * Get current date time.
     * @return String
     */
    private static String getDateTime() {
        return LocalDateTime.now().format(formatter);
    }

    /**
     * Get string representation of log type.
     * @param type Log type.
     * @return String
     */
    private static String getLogTypeText(Type type) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" |");
        switch (type) {
            case INFO:    stringBuilder.append("\033[36m").append("INFORMATION"); break; // Cyan
            case WARNING: stringBuilder.append("\033[33m").append("  WARNING  "); break; // Yellow
            case ERROR:   stringBuilder.append("\033[31m").append("   ERROR   "); break; // Red
            case SUCCESS: stringBuilder.append("\033[32m").append("  SUCCESS  "); break; // Green
            case LUA:     stringBuilder.append("\033[34m").append("    LUA    "); break; // Blue
            case DISPOSE: stringBuilder.append("\033[35m").append("  DISPOSE  "); break; // Magenta
        };
        stringBuilder.append("\033[0m").append("| ");
        return stringBuilder.toString();
    }
}
