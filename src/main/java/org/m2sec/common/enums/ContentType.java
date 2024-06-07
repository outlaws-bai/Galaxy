package org.m2sec.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;


/**
 * @author: outlaws-bai
 * @date: 2024/4/26 17:17
 * @description:
 */
@Getter
@AllArgsConstructor
public enum ContentType {
    NON_BODY(null),
    JSON("application/json"),
    FORM("application/x-www-form-urlencoded"),
    FORM_DATA("multipart/form-data"),
    XML("application/xml"),
    HTML("text/html"),
    TEXT("text/plain");

    @Nullable
    private final String headerValuePrefix;
}
