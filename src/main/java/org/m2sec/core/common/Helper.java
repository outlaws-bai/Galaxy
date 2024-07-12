package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.Galaxy;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 21:29
 * @description: 符合初始化及清理
 */
@Slf4j
public class Helper {

    public static Config initAndLoadConfig(MontoyaApi api) {
        String message;
        if (Files.exists(Paths.get(Constants.WORK_DIR))) {
            if (Files.exists(Paths.get(Constants.VERSION_STORAGE_FILE_PATH)) && !Constants.VERSION.equalsIgnoreCase(FileTools.readFileAsString(Constants.VERSION_STORAGE_FILE_PATH))) {
                // 使用过旧版本
                String randomString = generateRandomString(6);
                String bakDir = Constants.WORK_DIR + "." + randomString + ".bak";
                FileTools.renameDir(Constants.WORK_DIR, bakDir);
                message = Constants.UPDATE_VERSION_DEF + bakDir + ". \r\nGood luck.";
            } else {
                message = "Good luck.";
            }
        } else {
            message = "You seem to be using this plugin for the first time. \r\nGood luck.";
        }

        if (Galaxy.isInBurp()) {
            api.logging().logToOutput(message);
        } else {
            System.out.println(message);
        }

        // 创建必要的文件和路径
        FileTools.createDirs(Constants.WORK_DIR, // 插件工作路径
            Constants.TMP_FILE_DIR, // 临时文件路径
            Constants.EXTRACT_FILE_DIR // 提取文件路径
        );

        // cp resources 文件到工作目录下
        FileTools.writeFileIfEmptyOfResource(Constants.VERSION_STORAGE_FILE_NAME, Constants.VERSION_STORAGE_FILE_PATH);
        FileTools.writeFileIfEmptyOfResource(Constants.SETTING_FILE_NAME, Constants.SETTING_FILE_PATH);
        FileTools.writeFileIfEmptyOfResource(Constants.OPTION_FILE_NAME, Constants.OPTION_FILE_PATH);
        FileTools.copyDirResourcesToTargetDirIfEmpty(Constants.HTTP_HOOK_EXAMPLES_DIR_NAME,
            Constants.HTTP_HOOK_EXAMPLES_DIR);
        FileTools.copyDirResourcesToTargetDirIfEmpty(Constants.TEMPLATE_DIR_NAME, Constants.TEMPLATE_DIR);

        // 加载配置文件
        Config config = Config.ofDisk(api);

        // 初始化log
        Helper.initLogger(Constants.LOG_FILE_PATH, config.getSetting().getLogLevel().name());
        log.debug("load config success! {}", config);

        return config;
    }

    public static void initLogger(String logFilePath, String level) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("root");
        rootLogger.setLevel(Level.valueOf(level));
        ConsoleAppender<ILoggingEvent> consoleAppender = (ConsoleAppender<ILoggingEvent>) rootLogger.getAppender(
            "CONSOLE");
        PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(loggerContext);
        logEncoder.setPattern(((PatternLayoutEncoder) consoleAppender.getEncoder()).getPattern());
        logEncoder.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("FILE");
        fileAppender.setFile(logFilePath);
        fileAppender.setEncoder(logEncoder);
        fileAppender.start();
        rootLogger.addAppender(fileAppender);
    }


    public static void deleteLogFile() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (loggerContext == null) return;
        FileAppender<?> fileAppender = (FileAppender<?>) loggerContext.getLogger("root").getAppender("FILE");
        if (fileAppender == null) return;
        fileAppender.stop();
        FileTools.deleteFileIfExist(fileAppender.getFile());
    }

    public static void cleanTmpDir() {
        File tempFileDir = new File(Constants.TMP_FILE_DIR);
        if (tempFileDir.exists() && tempFileDir.isDirectory()) {
            FileTools.deleteFileIfExist(tempFileDir.listFiles());
        }
    }

    public static void initExceptionClean() {
        Helper.deleteLogFile();
        Helper.cleanTmpDir();
    }

    public static String generateRandomString(int length) {
        @SuppressWarnings("SpellCheckingInspection") String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

}
