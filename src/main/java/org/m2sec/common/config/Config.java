package org.m2sec.common.config;

import lombok.*;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Config {
    private HttpHookConfig httpHook;
    private CloudConfig cloud;
    private MixedConfig mixed;

    public static Config getDefault() {
        return new Config(HttpHookConfig.getDefault(), CloudConfig.getDefault(), MixedConfig.getDefault());
    }
}
