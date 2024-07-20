package org.m2sec.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:27
 * @description:
 */
@Getter
@AllArgsConstructor
public enum RunStatus {
    START("Start"), STOP("Stop");

    private final String display;
}
