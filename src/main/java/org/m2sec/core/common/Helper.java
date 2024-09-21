package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import org.graalvm.polyglot.Engine;
import org.m2sec.Galaxy;
import org.m2sec.core.utils.FactorUtil;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 21:29
 * @description: 符合初始化及清理
 */
public class Helper {

    public static void init(MontoyaApi api) {
        api.extension().setName(Constants.BURP_SUITE_EXT_NAME);
        api.logging().logToOutput(Constants.BURP_SUITE_EXT_INIT_DEF + "Version -> " + Constants.VERSION + "\r\n");
        checkJdk();
        checkGrpc();
        checkPythonAndJs();
        if (checkVersion(api)) {
            initWorkDir();
        }
    }

    public static boolean checkVersion(MontoyaApi api) {
        boolean buildWorkDir = false;
        String message;
        if (Files.exists(Paths.get(Constants.WORK_DIR))) {
            if (!Files.exists(Paths.get(Constants.VERSION_STORAGE_FILE_PATH)) || !Constants.VERSION.equalsIgnoreCase(FileTools.readFileAsString(Constants.VERSION_STORAGE_FILE_PATH))) {
                // 更新了版本
                String randomString = FactorUtil.randomString(6);
                String bakDir = Constants.WORK_DIR + "." + randomString + ".bak";
                FileTools.renameDir(Constants.WORK_DIR, bakDir);
                message = Constants.UPDATE_VERSION_DEF + bakDir + ". \r\nGood luck.";
                buildWorkDir = true;
            } else { // reload
                message = "Good luck.";
            }
        } else { // 第一次使用
            buildWorkDir = true;
            message = "You seem to be using this plugin for the first time. \r\nGood luck.";
        }
        if (Galaxy.isInBurp()) {
            api.logging().logToOutput(message);
        } else {
            System.out.println(message);
        }
        return buildWorkDir;
    }

    public static void checkJdk() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Constants.isUseJdk = compiler != null;
    }

    public static void checkPythonAndJs() {
        try (Engine engine = Engine.create()) {
            Constants.hasJs = engine.getLanguages().containsKey("js");
            Constants.hasPython = engine.getLanguages().containsKey("python");
        } catch (Exception ignore) {
            Constants.hasJs = false;
            Constants.hasPython = false;
        }
    }

    public static void checkGrpc() {
        Constants.hasGrpc = ReflectTools.canLoadClass("io.grpc.ManagedChannel");
    }


    public static void initWorkDir() {
        // 创建必要的文件和路径
        FileTools.createDirs(Constants.WORK_DIR, // 插件工作路径
            Constants.TMP_FILE_DIR, // 临时文件路径
            Constants.EXTRACT_FILE_DIR // 提取文件路径
        );
        // mv resource
        FileTools.mvResource(Constants.VERSION_STORAGE_FILE_NAME, Constants.WORK_DIR);
        FileTools.mvResource(Constants.SETTING_FILE_NAME, Constants.WORK_DIR);
        FileTools.mvResource(Constants.OPTION_FILE_NAME, Constants.WORK_DIR);
        // mv resources
        FileTools.mvResources(Constants.HTTP_HOOK_EXAMPLES_DIR_NAME, Constants.WORK_DIR);
        FileTools.mvResources(Constants.TEMPLATE_DIR_NAME, Constants.WORK_DIR);
    }


    /**
     * 初始化日志框架
     * 1. 增加文件输出
     * 2. 将文件输出copy一份到burp logging
     */
    public static void initLogger(MontoyaApi api, String level) {
        if (!Galaxy.isInBurp()) return;
        // 获取root logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("root");
        rootLogger.setLevel(Level.valueOf(level));
        // 获取xml中配置的 console appender
        ConsoleAppender<ILoggingEvent> consoleAppender = (ConsoleAppender<ILoggingEvent>) rootLogger.getAppender(
            "CONSOLE");
        // 创建并设置 file appender
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("FILE");
        fileAppender.setFile(Constants.LOG_FILE_PATH);
        fileAppender.setEncoder(consoleAppender.getEncoder());
        fileAppender.start();
        rootLogger.addAppender(fileAppender);
        // 创建并设置自定义 appender，将日志发送到burp
        BurpAppender burpAppender = new BurpAppender(api, (PatternLayoutEncoder) consoleAppender.getEncoder());
        burpAppender.setContext(loggerContext);
        burpAppender.setName("BURP");
        burpAppender.start();
        rootLogger.addAppender(burpAppender);
    }


    public static void deleteLogFile() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (loggerContext == null) return;
        FileAppender<?> fileAppender = (FileAppender<?>) loggerContext.getLogger("root").getAppender("FILE");
        if (fileAppender == null) return;
        fileAppender.stop();
        FileTools.deleteFiles(fileAppender.getFile());
    }

    public static void cleanTmpDir() {
        File tempFileDir = new File(Constants.TMP_FILE_DIR);
        if (tempFileDir.exists() && tempFileDir.isDirectory()) {
            FileTools.deleteFiles(tempFileDir.listFiles());
        }
    }

    public static void clean() {
        Helper.deleteLogFile();
        Helper.cleanTmpDir();
    }


    /**
     * 驼峰转蛇形
     */
    public static String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder snakeCase = new StringBuilder();
        char[] charArray = camelCase.toCharArray();
        boolean firstChar = true;

        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                if (!firstChar) {
                    snakeCase.append('_');
                }
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
            firstChar = false;
        }

        return snakeCase.toString();
    }


}
