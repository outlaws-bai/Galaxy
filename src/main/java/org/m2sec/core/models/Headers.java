package org.m2sec.core.models;

import burp.api.montoya.http.message.HttpHeader;
import org.m2sec.core.utils.HttpUtil;
import org.m2sec.rpc.HttpHook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class Headers extends Parameters<String> {

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

    public static Headers ofRpc(List<HttpHook.Header> rpcHeaders) {
        Headers retVal = new Headers();
        for (HttpHook.Header rpcHeader : rpcHeaders) {
            retVal.add(rpcHeader.getKey(), rpcHeader.getValue());
        }
        return retVal;
    }

    public List<String> getIgnoreCase(String key) {
        for (Map.Entry<String, List<String>> entry : entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    public String getFirstIgnoreCase(String key) {
        List<String> values = getIgnoreCase(key);
        if (values == null) {
            return null;
        } else {
            return values.get(0);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public Headers removeIgnoreCase(String key) {
        keySet().removeIf(key::equalsIgnoreCase);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Headers replaceIgnoreCase(String key, String value) {
        String oldKey = null;
        List<String> oldValues = null;
        for (Map.Entry<String, List<String>> entry : entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                oldKey = entry.getKey();
                oldValues = entry.getValue();
            }
        }
        if (oldKey != null) {
            replace(oldKey, oldValues, new ArrayList<>(List.of(value)));
        }
        return this;
    }

    public List<HttpHook.Header> toRpc() {
        List<HttpHook.Header> retVal = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : entrySet()) {
            for (String part : entry.getValue()) {
                retVal.add(HttpHook.Header.newBuilder().setKey(entry.getKey()).setValue(part).build());
            }
        }
        return retVal;
    }

    public String toRawString() {
        return HttpUtil.parametersToStr(this, "\r\n", ": ", false);
    }
}
