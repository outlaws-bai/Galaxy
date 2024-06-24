package org.m2sec.common.models;

import burp.api.montoya.http.message.HttpHeader;
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
public class Headers extends Parameters<String> {

    public Headers() {
        super(String.CASE_INSENSITIVE_ORDER);
    }


    public static Headers of(String str) {
        return HttpUtil.strToParameters(str, "\r\n\r\n", ":[ ]+", Headers.class);
    }

    public static Headers of(List<HttpHeader> headers) {
        Headers retVal = new Headers();
        for (HttpHeader header : headers) {
            retVal.add(header.name(), header.value());
        }
        return retVal;
    }

    public static Headers of(Map<String, HttpHook.StringList> map) {
        Headers retVal = new Headers();
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
        return HttpUtil.parametersToStr(this, "\r\n", ": ", false);
    }
}
