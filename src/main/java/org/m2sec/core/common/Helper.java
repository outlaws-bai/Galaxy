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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.Security;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 21:29
 * @description: 符合初始化及清理
 */
@Slf4j
public class Helper {

    public static Config initAndLoadConfig(MontoyaApi api) {
        // add加解密程序
        Security.addProvider(new BouncyCastleProvider());

        // 加载配置文件
        Config config = Config.ofDisk(api);

        // 初始化log
        Helper.initLogger(Constants.LOG_FILE_PATH, config.getSetting().getLogLevel().name());
        log.debug("load config success! {}", config);

        // 创建必要的文件和路径
        FileTools.createDirs(Constants.WORK_DIR, // 插件工作路径
            Constants.TMP_FILE_DIR, // 临时文件路径
            Constants.EXTRACT_FILE_DIR, // 提取文件路径
            Constants.HTTP_HOOK_EXAMPLES_FILE_DIR, // http hook examples
            Constants.TEMPLATE_FILE_DIR // templates
        );
        FileTools.createFiles(Constants.OPTION_FILE_PATH);

        // cp resources 文件到工作目录下
        FileTools.cpResourceFileToTarget("setting.yaml", Constants.WORK_DIR);
        FileTools.cpResourceFileToTarget("option.yaml", Constants.WORK_DIR);
        FileTools.copyResourceDirToTargetDir("examples", Constants.HTTP_HOOK_EXAMPLES_FILE_DIR);
        FileTools.copyResourceDirToTargetDir("templates", Constants.TEMPLATE_FILE_DIR);

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
        FileAppender<?> fileAppender = (FileAppender<?>) loggerContext.getLogger("root").getAppender("FILE");
        fileAppender.stop();
        FileTools.deleteFileIfExist(fileAppender.getFile());
    }

    public static void cleanTmpDir() {
        File tempFileDir = new File(Constants.TMP_FILE_DIR);
        if (tempFileDir.exists() && tempFileDir.isDirectory()) {
            FileTools.deleteFileIfExist(tempFileDir.listFiles());
        }
    }

}
