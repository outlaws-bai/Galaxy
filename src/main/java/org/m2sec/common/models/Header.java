package org.m2sec.common.models;

import burp.api.montoya.http.message.HttpHeader;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.rpc.HttpHook;

import javax.annotation.Nonnull;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
@Getter
@Setter
@ToString
public class Header {
    private String name;
    private String value;

    public Header(@Nonnull String name, @Nonnull String value) {
        this.name = name.toLowerCase();
        this.value = value;
    }

    public static Header of(HttpHeader header) {
        return new Header(header.name(), header.value());
    }

    public static Header of(HttpHook.Header header) {
        return new Header(header.getName(), header.getValue());
    }

    public HttpHeader toBurp() {
        return HttpHeader.httpHeader(this.name, this.value);
    }

    public HttpHook.Header toRpc() {
        return HttpHook.Header.newBuilder().setName(this.name).setValue(this.value).build();
    }
}
