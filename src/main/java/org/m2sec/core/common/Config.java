package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.m2sec.Galaxy;
import org.m2sec.core.utils.YamlUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 1:18
 * @description:
 */
@Getter
@ToString
@AllArgsConstructor
public class Config {
    /**
     * 项目配置
     */
    private Setting setting;
    /**
     * 上一次使用的选项
     */
    private Option option;


    public static Config ofDisk() {
        return ofDisk(null);
    }

    public static Config ofDisk(MontoyaApi api) {
        return ofDisk(api, Constants.OPTION_FILE_PATH, Constants.SETTING_FILE_PATH);
    }

    public static Config ofDisk(MontoyaApi api, String optionFilePath, String settingFilePath) {
        Setting setting = YamlUtil.fromYamlStr(FileTools.readFileAsString(settingFilePath), Setting.class);
        Option option = YamlUtil.fromYamlStr(FileTools.readFileAsString(optionFilePath), Option.class);
        Config config = new Config(setting, option);
        Constants.HTTP_STATIC_EXTENSIONS = config.getSetting().getStaticExtensions().split("\\|");
        if (Galaxy.isInBurp()) Constants.JAR_FILE_PATH = api.extension().filename();
        return config;
    }

    public void dumpOption() {
        option.setHookStart(false);
        option.setLinkageScanner(false);
        FileTools.writeFile(Constants.OPTION_FILE_PATH, YamlUtil.toYamlStr(option));
    }

    public void dumpSetting() {
        FileTools.writeFile(Constants.OPTION_FILE_PATH, YamlUtil.toYamlStr(option));
    }


}
