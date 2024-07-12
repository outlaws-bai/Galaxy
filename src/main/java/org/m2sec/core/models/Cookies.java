package org.m2sec.core.models;

import lombok.NoArgsConstructor;
import org.m2sec.core.common.Constants;
import org.m2sec.core.utils.HttpUtil;

import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Cookies extends Parameters<String> {

    public Cookies(Map<String, List<String>> multiMap) {
        super(multiMap);
    }

    public static Cookies of(String str) {
        return HttpUtil.strToParameters(str, Constants.HTTP_COOKIE_CONN, Constants.HTTP_H_C_Q_F_CONN,
            Cookies.class);
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, Constants.HTTP_COOKIES_CONN_RAW, Constants.HTTP_H_C_Q_F_CONN, true);
    }
}
