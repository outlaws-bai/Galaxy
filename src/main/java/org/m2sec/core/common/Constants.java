package org.m2sec.core.common;

import org.m2sec.core.utils.FileUtil;

import javax.swing.plaf.PanelUI;
import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:42
 * @description:
 */
public class Constants {

    public static final String BURP_SUITE_EXT_NAME = "Galaxy";
    public static final String VERSION = FileUtil.getVersion();

    public static final String BURP_SUITE_EXT_INIT_DEF = """
        Welcome to Galaxy
        Author -> outlaws-bai
        Github -> https://github.com/outlaws-bai/Galaxy
        """;

    public static final String HTTP_HOOK_GRPC_DEF = "You need to start a GRPC server and enter its address below.";
    public static final String HTTP_HOOK_JAVA_DEF = "You need to select the appropriate JAVA file and modify it until" +
        " it meets your needs.";

    public static final String WORK_DIR = System.getProperty("user.home") + File.separator + ".galaxy";

    public static final String TMP_FILE_DIR = WORK_DIR + File.separator + "tmp";

    public static final String EXTRACT_FILE_DIR = WORK_DIR + File.separator + "extract";
    public static final String HTTP_HOOK_EXAMPLES_FILE_DIR = WORK_DIR + File.separator + "examples";

    public static final String TEMPLATE_FILE_DIR = WORK_DIR + File.separator + "templates";
    public static final String CACHE_OPTION_FILE_PATH = WORK_DIR + File.separator + "cache.yaml";
    public static final String SETTING_FILE_PATH = WORK_DIR + File.separator + "setting.yaml";
    public static final String LOG_FILE_PATH = WORK_DIR + File.separator + "run.log";

    public static final String BYPASS_HOST_CHECK_TEMPLATE_FILE_PATH = TEMPLATE_FILE_DIR + File.separator +
        "bypassHostCheckTemplate.txt";

    public static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";

    public static final String HTTP_HEADER_HOOK_HEADER_KEY = "X-Galaxy-Http-Hook";

    public static final String HTTP_HEADER_HOST = "Host";

    public static final String HTTP_HEADER_COOKIE = "Cookie";

    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    public static final String DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " + "(KHTML, like Gecko) Chrome/120.0.0.0 " +
            "Safari/537" + ".36 Edg/120.0.0.0 M2Sec/1.0";


}
