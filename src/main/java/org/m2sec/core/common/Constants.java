package org.m2sec.core.common;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:42
 * @description:
 */
public class Constants {

    // global
    public static final String BURP_SUITE_EXT_NAME = "Galaxy";
    public static final String VERSION = FileTools.readResourceAsString(Constants.VERSION_STORAGE_FILE_NAME);

    public static final String BURP_SUITE_EXT_INIT_DEF = """
        Welcome to Galaxy
        Author -> outlaws-bai && tdyj && lwhispers
        Github -> https://github.com/outlaws-bai/Galaxy
        """;

    public static final String UPDATE_VERSION_DEF = "You have updated the version of Galaxy, but due to significant " +
        "version differences, your original work dir has been moved to ";

    public static String JAR_FILE_PATH;

    public static boolean isUseJdk = true;

    public static boolean hasGraalpy = true;

    public static boolean hasJs = true;

    public static boolean hasGrpc = true;

    public static boolean hasJython = true;

    // work file

    public static final String USER_HOME_DIR = System.getProperty("user.home");

    public static final String WORK_DIR = USER_HOME_DIR + File.separator + ".galaxy";

    public static final String TMP_FILE_DIR = WORK_DIR + File.separator + "tmp";

    public static final String EXTRACT_FILE_DIR = WORK_DIR + File.separator + "extract";
    public static final String HTTP_HOOK_EXAMPLES_DIR_NAME = "examples";

    public static final String TEMPLATE_DIR_NAME = "templates";
    public static final String HTTP_HOOK_EXAMPLES_DIR = WORK_DIR + File.separator + HTTP_HOOK_EXAMPLES_DIR_NAME;

    public static final String TEMPLATE_DIR = WORK_DIR + File.separator + TEMPLATE_DIR_NAME;

    public static final String OPTION_FILE_NAME = "option.yaml";
    public static final String SETTING_FILE_NAME = "setting.yaml";

    public static final String OPTION_FILE_PATH = WORK_DIR + File.separator + OPTION_FILE_NAME;
    public static final String SETTING_FILE_PATH = WORK_DIR + File.separator + SETTING_FILE_NAME;
    public static final String LOG_FILE_PATH = WORK_DIR + File.separator + "run.log";

    public static final String BYPASS_HOST_CHECK_TEMPLATE_FILE_PATH = TEMPLATE_DIR + File.separator +
        "bypassHostCheckTemplate.txt";

    public static final String VERSION_STORAGE_FILE_NAME = "version.txt";

    public static final String VERSION_STORAGE_FILE_PATH = WORK_DIR + File.separator + VERSION_STORAGE_FILE_NAME;

    // http

    public static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";

    public static final String HTTP_HEADER_HOOK_HEADER_KEY = "X-Galaxy-Http-Hook";

    public static final String HTTP_HEADER_HOST = "Host";

    public static final String HTTP_HEADER_COOKIE = "Cookie";

    public static final String HTTP_DEFAULT_VERSION = "HTTP/1.1";

    public static final String HTTP_DEFAULT_REASON = "OK";

    public static final String HTTP_QUERY_FORM_SEP = "&";

    public static final String HTTP_HEADER_CONN = ":[ ]*";

    public static final String HTTP_COOKIE_CONN = ";[ ]*";

    public static final String HTTP_H_C_Q_F_CONN = "=";

    public static final String HTTP_HEADERS_SEP = "\r\n";

    public static final String HTTP_HEADERS_CONN_RAW = ": ";
    public static final String HTTP_COOKIES_CONN_RAW = "; ";

    public static final String HTTP_PROTOCOL_DOMAIN_SEP = "://";

    public static String[] HTTP_STATIC_EXTENSIONS = (".css|.js|.jpg|.jpeg|.png|.gif|.bmp|.svg|.ico|.html|.htm|.xml|" +
        ".txt|" +
        ".pdf|.zip|.rar|.tar|.gz|.7z|.mp3|.mp4|.avi|.webm|.mov|.ogg|.wav|.swf|.woff|.ttf|.eot|.otf|.webp|.m4a|.m4v|" +
        ".flv|.wmv|.doc|.docx|.xls|.xlsx|.ppt|.pptx|.csv|.rtf").split("\\|");
    public static final String HTTP_DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " + "(KHTML, like Gecko) Chrome/120.0.0.0 " +
            "Safari/537" + ".36 Edg/120.0.0.0 M2Sec/1.0";

    // hook

    public static final String HOOK_FUNC_1 = "hookRequestToBurp";
    public static final String HOOK_FUNC_2 = "hookRequestToServer";
    public static final String HOOK_FUNC_3 = "hookResponseToBurp";
    public static final String HOOK_FUNC_4 = "hookResponseToClient";

    public static final String HOOK_BY_GRPC_IMPL_DEF = "Please start a GRPC server and enter its address below.";
    public static final String HOOK_BY_CODE_IMPL_DEF = "Select the appropriate script and modify it until" +
        " it meets your needs.";

    // file suffix
    public static final String JAVA_FILE_SUFFIX = ".java";

    public static final String JAVA_COMPILED_FILE_SUFFIX = ".class";

    public static final String GRAALPY_FILE_SUFFIX = ".graalpy.py";

    public static final String JYTHON_FILE_SUFFIX = ".jython.py";

    public static final String JS_FILE_SUFFIX = ".js";

    public static final String CRYPTO_PROVIDER = "BC";

}
