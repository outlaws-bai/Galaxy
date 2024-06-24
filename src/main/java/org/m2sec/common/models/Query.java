package org.m2sec.common.models;

import lombok.NoArgsConstructor;
import org.m2sec.common.utils.CompatUtil;
import org.m2sec.common.utils.HttpUtil;
import org.m2sec.rpc.HttpHook;

import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class Query extends Parameters<String> {
    public Query(Map<String, List<String>> map) {
        super(map);
    }

    public static Query of(String str) {
        return HttpUtil.strToParameters(str == null ? "" : str, "&", "=", Query.class);
    }

    public static Query of(Map<String, HttpHook.StringList> map) {
        Query retVal = new Query();
        for (Map.Entry<String, HttpHook.StringList> entry : map.entrySet()) {
            for (String str : entry.getValue().getValuesList()) {
                retVal.add(entry.getKey(), str);
            }
        }
        return retVal;
    }

    public Map<String, HttpHook.StringList> toRpc() {
        return CompatUtil.parametersToRpc(this);
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, "&", "=", true);
    }
}
