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
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.m2sec.abilities.MasterHttpHandler;
import org.m2sec.abilities.MaterProxyHandler;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.WorkExecutor;
import org.m2sec.core.enums.ContentType;
import org.m2sec.core.enums.RuntimeEnv;
import org.m2sec.core.common.Constants;
import org.m2sec.core.utils.FileUtil;
import org.m2sec.panels.about.AboutPanel;
import org.m2sec.panels.httphook.HttpHookPanel;
import org.m2sec.panels.MainPanel;
import org.m2sec.panels.setting.SettingPanel;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.Security;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:42
 * @description:
 */
@Slf4j
public class Galaxy implements BurpExtension {


    private static RuntimeEnv env = RuntimeEnv.LOCAL;

    public static Config config;

    @Override
    public void initialize(MontoyaApi api) {
        env = RuntimeEnv.BURP;
        api.extension().setName(Constants.BURP_SUITE_EXT_NAME + "-" + Constants.VERSION);
        api.logging().logToOutput(Constants.BURP_SUITE_EXT_INIT_DEF + "Version -> " + Constants.VERSION);
        // 初始化
        init();
        // 加载配置
        config = Config.ofWorkDir();
        log.debug("load config success! {}", config);
        // init log
        initLogger(Constants.LOG_FILE_PATH, config.getSetting().getLogLevel().name());
        // 注册UI
        api.userInterface().registerSuiteTab(Constants.BURP_SUITE_EXT_NAME, getMainPanel(config, api));
        // 注册插件能力
        registerAbility(api);
        // 注册销毁事件
        api.extension().registerUnloadingHandler(() -> this.destroy(config));
    }

    private void init() {
        Security.addProvider(new BouncyCastleProvider());

        // 创建必要的文件和路径
        FileUtil.createDirs(Constants.WORK_DIR, // 插件工作路径
            Constants.TMP_FILE_DIR, // 临时文件路径
            Constants.EXTRACT_FILE_DIR, // 提取文件路径
            Constants.HTTP_HOOK_EXAMPLES_FILE_DIR, // http hook examples
            Constants.TEMPLATE_FILE_DIR // templates
        );
        FileUtil.createFiles(Constants.CACHE_OPTION_FILE_PATH);

        // cp resources 文件到工作目录下
        FileUtil.cpResourceFileToTarget("setting.yaml", Constants.WORK_DIR);
        FileUtil.cpResourceFileToTarget("cache.yaml", Constants.WORK_DIR);
        FileUtil.copyResourceDirToTargetDir("examples", Constants.HTTP_HOOK_EXAMPLES_FILE_DIR);
        FileUtil.copyResourceDirToTargetDir("templates", Constants.TEMPLATE_FILE_DIR);
    }

    public static MainPanel getMainPanel(Config config, MontoyaApi api) {
        HttpHookPanel httpHookPanel = new HttpHookPanel(config.getCacheOption(), api);
        SettingPanel settingPanel = new SettingPanel(config.getSetting(), api);
        AboutPanel aboutPanel = new AboutPanel(api);
        MainPanel mainPanel = new MainPanel(httpHookPanel, settingPanel, aboutPanel);
        if (isInBurp()) {
            api.userInterface().applyThemeToComponent(settingPanel);
            api.userInterface().applyThemeToComponent(aboutPanel);
        }
        return mainPanel;
    }

    private void registerAbility(MontoyaApi api) {
        // 注册menu item
//        burp.userInterface().registerContextMenuItemsProvider(new MasterContextMenuItemsProvider());
        // 注册unloading hook
//        burp.extension().registerUnloadingHandler(this::destroy);
        // 注册http hook 能力
        api.http().registerHttpHandler(new MasterHttpHandler(api));
        MaterProxyHandler materProxyHandler = new MaterProxyHandler(api);
        api.proxy().registerRequestHandler(materProxyHandler);
        api.proxy().registerResponseHandler(materProxyHandler);
        // 注册payload生成器
//        burp.intruder().registerPayloadGeneratorProvider(new BypassUrlGeneratorProviderProvider());
//        burp.intruder().registerPayloadGeneratorProvider(new BypassPathGeneratorProviderProvider());
//        burp.intruder().registerPayloadGeneratorProvider(new FuzzSensitivePathGeneratorProviderProvider());
//        burp.intruder().registerPayloadGeneratorProvider(new FuzzSensitivePathAndBypassGeneratorProviderProvider());
    }


    /**
     * 1. dump本次使用中的选项到cache.yaml
     * 2. 删除日志文件
     * 3. 清空tmp目录
     *
     * @param config 全局配置
     */
    private void destroy(Config config) {
        config.dumpCache();

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        FileAppender<?> fileAppender = (FileAppender<?>) loggerContext.getLogger("root").getAppender("FILE");
        fileAppender.stop();
        FileUtil.deleteFileIfExist(fileAppender.getFile());

        File tempFileDir = new File(Constants.TMP_FILE_DIR);
        if (tempFileDir.exists() && tempFileDir.isDirectory()) {
            FileUtil.deleteFileIfExist(tempFileDir.listFiles());
        }
        WorkExecutor.INSTANCE.shutdown();
    }


    public static boolean isInBurp() {
        return env.equals(RuntimeEnv.BURP);
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

}
