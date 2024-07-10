package org.m2sec.core.models;

import lombok.NoArgsConstructor;
import org.m2sec.core.utils.HttpUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Form extends Parameters<String> {


    public static Form of(String str) {
        return HttpUtil.strToParameters(str, "&", "=", Form.class);
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, "&", "=", true);
    }
}
