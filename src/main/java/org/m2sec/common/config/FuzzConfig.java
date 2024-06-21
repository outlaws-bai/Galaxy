package org.m2sec.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FuzzConfig {
    private boolean swaggerGeneratedRequestAutoSend;
}
