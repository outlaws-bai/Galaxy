package org.m2sec.common;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class Constants {

    public static final String BURP_SUITE_EXT_NAME = "Galaxy";

    public static final String BURP_SUITE_EXT_INIT_DEF =
            "Welcome to Galaxy\nAuthor -> outlaws-bai\nGithub -> https://github.com/outlaws-bai/Galaxy\n";

    public static final String WORK_DIR =
            System.getProperty("user.home") + File.separator + ".galaxy";

    public static final String TMP_FILE_DIR = WORK_DIR + File.separator + "tmp";

    public static final String DICT_FILE_DIR = WORK_DIR + File.separator + "dict";
    public static final String EXTRACT_INFO_FILE_DIR = WORK_DIR + File.separator + "extractInfo";

    public static final String CONFIG_FILE_PATH = WORK_DIR + File.separator + "config.yaml";

    public static final String BYPASS_URL_DICT_FILE_PATH =
            Constants.DICT_FILE_DIR + File.separator + "bypassUrlDict.txt";

    public static final String FUZZ_SENSITIVE_PATH_DICT_FILE_PATH =
            Constants.DICT_FILE_DIR + File.separator + "fuzzSensitivePathDict.txt";

    public static final String HTTP_HEADER_CONTENT_LENGTH = "content-length";
    public static final String HTTP_HEADER_CONTENT_TYPE = "content-type";

    public static final String HTTP_HEADER_USER_AGENT = "user-agent";

    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    public static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0 M2Sec/1.0";

    public static final String HTTP_HOOK_HEADER_KEY = "x-galaxy-http-hook";

    public static final String CRYPTO_PROVIDER_BC = "BC";

    public static final String HTTP_HOOK_JAVA_FILE_PATH = Constants.WORK_DIR + File.separator + "Hook.java";
}
