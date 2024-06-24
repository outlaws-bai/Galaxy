package org.m2sec.common.models;

import lombok.NoArgsConstructor;
import org.m2sec.common.utils.HttpUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Cookies extends Parameters<String> {


    public static Cookies of(String str) {
        return HttpUtil.strToParameters(str, ";[ ]+", "=", Cookies.class);
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, "; ", "=", true);
    }
}
