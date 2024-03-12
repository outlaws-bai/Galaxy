package org.m2sec.common.enums;

import lombok.AllArgsConstructor;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
@AllArgsConstructor
public enum HttpContentType {
    JSON("application/json"),
    FORM("application/x-www-form-urlencoded"),
    FORM_DATA("multipart/form-data"),
    XML("application/xml"),
    NON_BODY("non/body"),
    TEXT("text");

    final String contentType;

    @Override
    public String toString() {
        return contentType;
    }

    public static HttpContentType of(String data) {
        for (HttpContentType contentType : HttpContentType.values()) {
            if (data.startsWith(contentType.toString())) {
                return contentType;
            }
        }
        return JSON;
    }
}
