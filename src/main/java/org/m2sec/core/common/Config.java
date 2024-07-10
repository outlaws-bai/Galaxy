package org.m2sec.core.common;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.m2sec.core.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 1:18
 * @description:
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Config {
    /**
     * 项目配置
     */
    private Setting setting;
    /**
     * 上一次使用的选项
     */
    private HashMap<String, Object> cacheOption;


    public static Config ofWorkDir() {
        return ofWorkDir(Constants.CACHE_OPTION_FILE_PATH, Constants.SETTING_FILE_PATH);
    }

    public static Config ofWorkDir(String cacheFilePath, String settingFilePath) {
        Setting setting = YamlParser.fromYamlStr(FileUtil.readFileAsString(settingFilePath), Setting.class);
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        HashMap<String, Object> cache = YamlParser.fromYamlStr(FileUtil.readFileAsString(cacheFilePath), type);

        return new Config(setting, cache);
    }

    public void dumpCache() {
    }

    public void dumpSetting() {
    }


}
