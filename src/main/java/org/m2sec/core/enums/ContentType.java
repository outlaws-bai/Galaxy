package org.m2sec.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;



/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@AllArgsConstructor
public enum ContentType {
    NON_BODY(null), JSON("application/json"), FORM("application/x-www-form-urlencoded"),
    FORM_DATA("multipart/form" + "-data"), XML("application/xml"), HTML("text/html"), TEXT("text/plain");

    private final String headerValuePrefix;
}
