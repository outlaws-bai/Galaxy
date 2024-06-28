package org.m2sec;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.m2sec.burp.http.HttpTrafficAutoModificationHttpHandler;
import org.m2sec.burp.menu.MasterContextMenuItemsProvider;
import org.m2sec.burp.proxy.HttpTrafficAutoModificationProxyRequestHandler;
import org.m2sec.burp.proxy.HttpTrafficAutoModificationProxyResponseHandler;
import org.m2sec.common.Constants;
import org.m2sec.common.WorkExecutor;
import org.m2sec.common.config.Config;
import org.m2sec.common.enums.OperatingEnv;
import org.m2sec.common.parsers.YamlParser;
import org.m2sec.common.utils.CompatUtil;
import org.m2sec.common.utils.FileUtil;
import org.m2sec.modules.bypass.intruder.BypassPathGeneratorProviderProvider;
import org.m2sec.modules.bypass.intruder.BypassUrlGeneratorProviderProvider;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathAndBypassGeneratorProviderProvider;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathGeneratorProviderProvider;
import org.m2sec.modules.traffic.hook.AbstractHttpHookService;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.Arrays;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description: 入口
 */
@Slf4j
public class GalaxyMain implements BurpExtension {

    public static MontoyaApi burpApi;

    public static Config config;

    public static OperatingEnv env = OperatingEnv.LOCAL;


    @Override
    public void initialize(MontoyaApi montoyaApi) {
        burpApi = montoyaApi;
        env = OperatingEnv.BURP;
        burpApi.extension().setName(Constants.BURP_SUITE_EXT_NAME);
        burpApi.logging().logToOutput(Constants.BURP_SUITE_EXT_INIT_DEF);
        init();
        registerUI();
        registerAbility();
    }

    @SuppressWarnings({"DataFlowIssue", "resource"})
    private void init() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            // 创建必要的文件路径
            FileUtil.createDirs(Constants.WORK_DIR, // 插件工作根路径
                Constants.TMP_FILE_DIR, // 临时文件路径
                Constants.EXTRACT_INFO_FILE_DIR, // 提取文件路径
                Constants.DICT_FILE_DIR // 字典路径
            );
            // 创建必要的文件
            FileUtil.createFiles(Constants.CONFIG_FILE_PATH, Constants.BYPASS_URL_DICT_FILE_PATH,
                Constants.FUZZ_SENSITIVE_PATH_DICT_FILE_PATH, Constants.STATIC_EXTENSION_DICT_FILE_PATH,
                Constants.HTTP_HOOK_JAVA_FILE_PATH);
            ClassLoader classLoader = this.getClass().getClassLoader();
            FileUtil.writeToFileIfEmpty(Constants.BYPASS_URL_DICT_FILE_PATH,
                classLoader.getResourceAsStream(new File(Constants.BYPASS_URL_DICT_FILE_PATH).getName()).readAllBytes());
            FileUtil.writeToFileIfEmpty(Constants.FUZZ_SENSITIVE_PATH_DICT_FILE_PATH,
                classLoader.getResourceAsStream(new File(Constants.FUZZ_SENSITIVE_PATH_DICT_FILE_PATH).getName()).readAllBytes());
            FileUtil.writeToFileIfEmpty(Constants.STATIC_EXTENSION_DICT_FILE_PATH,
                classLoader.getResourceAsStream(new File(Constants.STATIC_EXTENSION_DICT_FILE_PATH).getName()).readAllBytes());
            FileUtil.writeToFileIfEmpty(Constants.HTTP_HOOK_JAVA_FILE_PATH,
                classLoader.getResourceAsStream(new File(Constants.HTTP_HOOK_JAVA_FILE_PATH).getName()).readAllBytes());
            FileUtil.writeToFileIfEmpty(Constants.CONFIG_FILE_PATH, classLoader.getResourceAsStream("config" +
                ".default.yaml").readAllBytes());
            config = YamlParser.fromYamlStr(FileUtil.readFileAsString(Constants.CONFIG_FILE_PATH), Config.class);
            AbstractHttpHookService.trySetService();
            initLogger(Constants.LOG_FILE_PATH, config.getLogLevel());
            log.debug("load config success! {}", config.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void registerUI() {
        JPanel ui = new JPanel();
        JButton reloadConfigButton = new JButton("ReloadConfig");
        JButton openWorkDirButton = new JButton("OpenWorkDir");
        ui.add(reloadConfigButton);
        ui.add(openWorkDirButton);
        reloadConfigButton.addActionListener(e -> {
            try {
                config = YamlParser.fromYamlStr(FileUtil.readFileAsString(Constants.CONFIG_FILE_PATH), Config.class);
                AbstractHttpHookService.trySetService();
                log.debug("load config success! {}", config.toString());
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        openWorkDirButton.addActionListener(e -> {
            try {
                CompatUtil.openFileManager(Constants.WORK_DIR);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        burpApi.userInterface().registerSuiteTab(Constants.BURP_SUITE_EXT_NAME, ui);
    }

    private void registerAbility() {
        // 注册menu item
        burpApi.userInterface().registerContextMenuItemsProvider(new MasterContextMenuItemsProvider());
        // 注册unloading hook
        burpApi.extension().registerUnloadingHandler(this::destroy);
        // 注册http hook
        burpApi.http().registerHttpHandler(new HttpTrafficAutoModificationHttpHandler());
        burpApi.proxy().registerRequestHandler(new HttpTrafficAutoModificationProxyRequestHandler());
        burpApi.proxy().registerResponseHandler(new HttpTrafficAutoModificationProxyResponseHandler());
        // 注册payload生成器
        burpApi.intruder().registerPayloadGeneratorProvider(new BypassUrlGeneratorProviderProvider());
        burpApi.intruder().registerPayloadGeneratorProvider(new BypassPathGeneratorProviderProvider());
        burpApi.intruder().registerPayloadGeneratorProvider(new FuzzSensitivePathGeneratorProviderProvider());
        burpApi.intruder().registerPayloadGeneratorProvider(new FuzzSensitivePathAndBypassGeneratorProviderProvider());
    }

    private void destroy() {
        // 停止fileAppender，释放其fd
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        FileAppender<?> fileAppender = (FileAppender<?>) loggerContext.getLogger("root").getAppender("FILE");
        fileAppender.stop();
        // 删除日志文件
        FileUtil.deleteFileIfExist(fileAppender.getFile());
        // 清空.counter/tmp目录
        File tempFileDir = new File(Constants.TMP_FILE_DIR);
        if (tempFileDir.exists() && tempFileDir.isDirectory()) {
            FileUtil.deleteFileIfExist(tempFileDir.listFiles());
        }
        WorkExecutor.INSTANCE.shutdown();
        GalaxyMain.burpApi.logging().logToOutput("Unloading Galaxy. See you later...");
    }

    public static void initLogger(@NonNull String logFilePath, @NonNull String level) {
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
}
