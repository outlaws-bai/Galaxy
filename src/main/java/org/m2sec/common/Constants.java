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
            "Welcome to Galaxy\nAuthor -> outlaws-bai & tdyj\nGithub -> https://github.com/outlaws-bai/Galaxy\n";

    public static final String WORK_DIR =
            System.getProperty("user.dir") + File.separator + ".galaxy";

    public static final String TMP_FILE_DIR = WORK_DIR + File.separator + "tmp";
    public static final String FUZZ_DICTS_FILE_DIR = WORK_DIR + File.separator + "fuzzDicts";

    public static final String CONFIG_FILE_PATH = WORK_DIR + File.separator + "config.yaml";

    public static final String HTTP_HEADER_CONTENT_LENGTH = "content-length";
    public static final String HTTP_HEADER_CONTENT_TYPE = "content-type";

    public static final String HTTP_HEADER_USER_AGENT = "user-agent";

    public static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0 M2Sec/1.0";

    public static final String HTTP_HOOK_HEADER_KEY = "x-galaxy-http-hook";

}
