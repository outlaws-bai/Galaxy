package org.m2sec;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.googlecode.aviator.AviatorEvaluator;
import org.m2sec.burp.http.HttpHookHttpHandler;
import org.m2sec.burp.menu.MasterContextMenuItemsProvider;
import org.m2sec.burp.proxy.HttpHookProxyRequestHandler;
import org.m2sec.burp.proxy.HttpHookProxyResponseHandler;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.config.Config;
import org.m2sec.common.enums.LogLevel;
import org.m2sec.common.enums.OperatingEnv;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.parsers.YamlParser;
import org.m2sec.modules.bypass.intruder.BypassPathGeneratorProviderProvider;
import org.m2sec.modules.bypass.intruder.BypassUrlGeneratorProviderProvider;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathAndBypassGeneratorProviderProvider;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathGeneratorProviderProvider;
import org.m2sec.modules.httphook.HttpHookTransfer;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

    public static OperatingEnv env = OperatingEnv.LOCAL;

    public static ExecutorService workExecutor = Executors.newFixedThreadPool(5);

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
        try {
            Log.logLevel = LogLevel.DEBUG;
            env = OperatingEnv.BURP;
            // 在表达式引擎中增加Request、Response的所有实例方法
            AviatorEvaluator.addInstanceFunctions("Request", Request.class);
            AviatorEvaluator.addInstanceFunctions("Response", Response.class);

            File workDir = new File(Constants.WORK_DIR);
            if (!workDir.exists()) {
                // 不存在，创建文件夹
                if (!workDir.mkdirs()) throw new RuntimeException("Galaxy work dir created fail.");
            }
            // 初始化.galaxy/tmp目录
            File tmpFileDir = new File(Constants.TMP_FILE_DIR);
            if (!tmpFileDir.exists()) {
                if (!tmpFileDir.mkdirs()) {
                    throw new RuntimeException("Galaxy tmp file dir created fail.");
                }
            }
            // 初始化.galaxy/fuzzDicts目录
            File fuzzDictsFileDir = new File(Constants.FUZZ_DICTS_FILE_DIR);
            if (!fuzzDictsFileDir.exists()) {
                if (!fuzzDictsFileDir.mkdirs()) {
                    throw new RuntimeException("Galaxy fuzzDicts file dir created fail.");
                }
            }
            // 初始化配置文件
            File configFile = new File(Constants.CONFIG_FILE_PATH);
            if (configFile.exists() && configFile.isFile()) {
                String content = new String(Files.readAllBytes(Paths.get(configFile.getPath())));
                config = YamlParser.fromYamlStr(content, Config.class);
                log.debug("config.yaml is exist. %s", config);
            } else {
                config = Config.getDefault();
                try {
                    String yamlStr = YamlParser.toYamlStr(config);
                    try (FileWriter writer = new FileWriter(configFile.getPath())) {
                        writer.write(yamlStr);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.debug("config.yaml is not exist. use default and write");
            }
        } catch (IllegalAccessException | NoSuchMethodException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerUI() {
        JPanel ui = new JPanel();
        JButton reloadButton = new JButton("reload config");
        JButton dumpButton = new JButton("dump config");
        ui.add(reloadButton);
        ui.add(dumpButton);
        reloadButton.addActionListener(
                l -> {
                    try {
                        File configFile = new File(Constants.CONFIG_FILE_PATH);
                        String content =
                                new String(Files.readAllBytes(Paths.get(configFile.getPath())));
                        config = YamlParser.fromYamlStr(content, Config.class);
                        if (config.getHttpHook().isStart()) {
                            HttpHookTransfer.client =
                                    GalaxyMain.config.getHttpHook().getRpcClient();
                        } else {
                            if (HttpHookTransfer.client != null) {
                                HttpHookTransfer.client.shutdown();
                            }
                            HttpHookTransfer.client = null;
                        }
                        log.info("load config success! %s", config.toString());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                                null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
        dumpButton.addActionListener(
                l -> {
                    try (FileWriter writer = new FileWriter(Constants.CONFIG_FILE_PATH)) {
                        writer.write(YamlParser.toYamlStr(config));
                    } catch (IOException e) {
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
