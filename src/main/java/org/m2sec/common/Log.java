package org.m2sec.common;

import org.m2sec.GalaxyMain;
import org.m2sec.common.enums.LogLevel;
import org.m2sec.common.enums.OperatingEnv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class Log {

    public static LogLevel logLevel = LogLevel.DEBUG;

    public String moduleName;

    public Log(Class<?> clazz) {
        moduleName = clazz.getSimpleName();
    }

    public void debug(String format, Object... args) {
        if (logLevel.equals(LogLevel.DEBUG)) {
            String msg = formatMessage("DEBUG", format, args);
            if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
                GalaxyMain.burpApi.logging().logToOutput(msg);
            } else {
                System.out.println(msg);
            }
        }
    }

    public void info(String format, Object... args) {
        String msg = formatMessage("INFO", format, args);
        if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
            GalaxyMain.burpApi.logging().logToOutput(msg);
        } else {
            System.out.println(msg);
        }
    }

    public void infoEvent(String format, Object... args) {
        String msg = String.format(format, args);
        if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
            GalaxyMain.burpApi.logging().raiseInfoEvent(msg);
        } else {
            System.out.println(msg);
        }
    }

    public void error(String format, Object... args) {
        String msg = formatMessage("ERROR", format, args);
        if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
            GalaxyMain.burpApi.logging().logToOutput(msg);
        } else {
            System.out.println(msg);
        }
    }

    public void errorEvent(String format, Object... args) {
        String msg = String.format(format, args);
        if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
            GalaxyMain.burpApi.logging().raiseErrorEvent(msg);
        } else {
            System.out.println(msg);
        }
    }

    public void exception(Throwable exc, String format, Object... args) {
        String msg = formatMessage("ERROR", format, args);
        if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
            GalaxyMain.burpApi.logging().logToOutput(msg);
            GalaxyMain.burpApi.logging().logToError(exc);
        } else {
            System.out.println(msg);
        }
    }

    private void safeError(String msg) {
        if (GalaxyMain.env.equals(OperatingEnv.BURP)) {
            GalaxyMain.burpApi.logging().logToOutput(msg);
        } else {
            System.out.println(msg);
        }
    }

    private String formatMessage(String level, String format, Object... args) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        String result;
        try {
            result =
                    String.format(
                            "[%s: %s] [%s]: %s",
                            formattedTime, level, this.moduleName, String.format(format, args));
        } catch (MissingFormatArgumentException e) {
            safeError(
                    "format error. e: "
                            + e.getMessage()
                            + " , format: "
                            + format
                            + ", args: "
                            + Arrays.toString(args));
            result =
                    String.format(
                            "[%s: %s] [%s]: %s", formattedTime, level, this.moduleName, format);
        }

        return result;
    }
}
