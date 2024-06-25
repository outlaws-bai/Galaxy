package org.m2sec.common.models;

import lombok.NoArgsConstructor;
import org.m2sec.common.utils.HttpUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Query extends Parameters<String> {

    public static Query of(String str) {
        return HttpUtil.strToParameters(str, "&", "=", Query.class);
    }


    public String toRawString() {
        return HttpUtil.parametersToStr(this, "&", "=", true);
    }
}
