package org.m2sec;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import org.m2sec.burp.http.HttpHookHttpHandler;
import org.m2sec.burp.menu.MasterContextMenuItemsProvider;
import org.m2sec.burp.proxy.HttpHookProxyRequestHandler;
import org.m2sec.burp.proxy.HttpHookProxyResponseHandler;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.config.Config;
import org.m2sec.common.config.HttpTrafficAutoModificationConfig;
import org.m2sec.common.enums.HttpHookService;
import org.m2sec.common.enums.LogLevel;
import org.m2sec.common.enums.OperatingEnv;
import org.m2sec.common.parsers.YamlParser;
import org.m2sec.common.utils.FileUtil;
import org.m2sec.modules.bypass.intruder.BypassPathGeneratorProviderProvider;
import org.m2sec.modules.bypass.intruder.BypassUrlGeneratorProviderProvider;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathAndBypassGeneratorProviderProvider;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathGeneratorProviderProvider;
import org.m2sec.modules.traffic.hook.AbstractHttpHookService;
import org.m2sec.modules.traffic.hook.RpcService;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description: 入口
 */
public class GalaxyMain implements BurpExtension {

    public static MontoyaApi burpApi;

    public static Config config;

    public static AbstractHttpHookService httpHookService;

    public static OperatingEnv env = OperatingEnv.LOCAL;

    public static ExecutorService workExecutor = Executors.newFixedThreadPool(5);

    public static Map<HttpHookService, AbstractHttpHookService> httpHookServiceMap =
            new HashMap<>(Map.of(HttpHookService.RPC, new RpcService()));

    private static final Log log = new Log(GalaxyMain.class);

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        burpApi = montoyaApi;
        burpApi.extension().setName(Constants.BURP_SUITE_EXT_NAME);
        burpApi.logging().logToOutput(Constants.BURP_SUITE_EXT_INIT_DEF);
        init();
        registerUI();
        registerAbility();
    }

    private void init() {
        Log.logLevel = LogLevel.DEBUG;
        env = OperatingEnv.BURP;
        if (Files.exists(Paths.get(Constants.WORK_DIR))) {
            loadConfig();
            log.debug("config.yaml is exist. %s", config);
        } else {
            // 创建必要的文件路径
            FileUtil.createDirs(
                    Constants.WORK_DIR, // 插件工作根路径
                    Constants.TMP_FILE_DIR, // 临时文件路径
                    Constants.EXTRACT_INFO_FILE_DIR, // 提取文件路径
                    Constants.DICT_FILE_DIR // 字典路径
            );
            // 创建必要的文件
            FileUtil.createFiles(
                    Constants.CONFIG_FILE_PATH,
                    Constants.BYPASS_URL_DICT_FILE_PATH,
                    Constants.FUZZ_SENSITIVE_PATH_DICT_FILE_PATH);
            log.debug("config.yaml is not exist. use default and write");
            config = Config.getDefault();
            FileUtil.writeToFileIfEmpty(
                    Constants.CONFIG_FILE_PATH, YamlParser.toYamlStr(config));
        }
    }

    private void loadConfig() {
        config =
                YamlParser.fromYamlStr(
                        FileUtil.readFileAsString(Constants.CONFIG_FILE_PATH), Config.class);
        HttpTrafficAutoModificationConfig.HookConfig hookConfig =
                config.getHttpTrafficAutoModificationConfig().getHookConfig();
        if (hookConfig.isStart() && hookConfig.getService() != null) {
            httpHookService = httpHookServiceMap.get(hookConfig.getService());
            httpHookService.init();
            log.debug("httpHookService: " + httpHookService.getClass().getSimpleName());
        } else if (httpHookService != null) {
            httpHookService.destroy();
            httpHookService = null;
        }
    }

    private void registerUI() {
        JPanel ui = new JPanel();
        JButton reloadButton = new JButton("reload config");
        ui.add(reloadButton);
        reloadButton.addActionListener(
                l -> {
                    try {
                        loadConfig();
                        log.debug("load config success! %s", config.toString());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                                null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
        burpApi.userInterface().registerSuiteTab(Constants.BURP_SUITE_EXT_NAME, ui);
    }

    private void registerAbility() {
        // 注册menu item
        burpApi.userInterface()
                .registerContextMenuItemsProvider(new MasterContextMenuItemsProvider());
        // 注册unloading hook
        burpApi.extension().registerUnloadingHandler(this::destroy);
        // 注册http hook
        burpApi.http().registerHttpHandler(new HttpHookHttpHandler());
        burpApi.proxy().registerRequestHandler(new HttpHookProxyRequestHandler());
        burpApi.proxy().registerResponseHandler(new HttpHookProxyResponseHandler());
        // 注册payload生成器
        burpApi.intruder()
                .registerPayloadGeneratorProvider(new BypassUrlGeneratorProviderProvider());
        burpApi.intruder()
                .registerPayloadGeneratorProvider(new BypassPathGeneratorProviderProvider());
        burpApi.intruder()
                .registerPayloadGeneratorProvider(new FuzzSensitivePathGeneratorProviderProvider());
        burpApi.intruder()
                .registerPayloadGeneratorProvider(
                        new FuzzSensitivePathAndBypassGeneratorProviderProvider());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void destroy() {
        // 清空.counter/tmp目录
        File tempFileDir = new File(Constants.TMP_FILE_DIR);
        if (tempFileDir.exists() && tempFileDir.isDirectory()) {
            // 获取目录下的所有文件和子目录
            File[] files = tempFileDir.listFiles();
            // 遍历删除文件
            if (files != null) Arrays.stream(files).filter(File::isFile).forEach(File::delete);
        }
        workExecutor.shutdown();
        log.info("Unloading Galaxy. See you later...");
    }
}
