package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.m2sec.core.utils.FileUtil;
import org.m2sec.core.utils.YamlUtil;

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
    private CacheInfo cacheOption;

    private String jarFilePath;

    public Config(MontoyaApi api){
        jarFilePath = api.extension().filename();
    }

    public void patchFromWorkDir() {
        patchFromWorkDir(Constants.CACHE_OPTION_FILE_PATH, Constants.SETTING_FILE_PATH);
    }

    public void patchFromWorkDir(String cacheFilePath, String settingFilePath) {
        Setting setting = YamlUtil.fromYamlStr(FileUtil.readFileAsString(settingFilePath), Setting.class);
        CacheInfo cacheOption = YamlUtil.fromYamlStr(FileUtil.readFileAsString(cacheFilePath), CacheInfo.class);
        this.setting = setting;
        this.cacheOption = cacheOption;
        Constants.STATIC_EXTENSIONS = setting.getStaticExtensions();
    }

    public void dumpCache() {
        FileUtil.writeFile(Constants.CACHE_OPTION_FILE_PATH, YamlUtil.toYamlStr(setting));
    }

    public void dumpSetting() {
    }


}
