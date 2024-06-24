package org.m2sec.common.models;

import lombok.NoArgsConstructor;
import org.m2sec.common.utils.HttpUtil;

import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Form extends Parameters<String> {

    public Form(Map<String, List<String>> map) {
        super(map);
    }

    public static Form of(String str) {
        return HttpUtil.strToParameters(str, "&", "=", Form.class);
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, "&", "=", true);
    }
}
