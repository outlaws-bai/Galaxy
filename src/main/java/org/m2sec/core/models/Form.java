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
public class Form extends Parameters<String> {

    public Form(Map<String, List<String>> multiMap) {
        super(multiMap);
    }

    public static Form of(String str) {
        return HttpUtil.strToParameters(str, Constants.HTTP_QUERY_FORM_SEP, Constants.HTTP_H_C_Q_F_CONN, Form.class);
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, Constants.HTTP_QUERY_FORM_SEP, Constants.HTTP_H_C_Q_F_CONN, true);
    }
}
