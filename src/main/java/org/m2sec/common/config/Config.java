package org.m2sec.common.config;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

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
    private HttpTrafficAutoModificationConfig httpTrafficAutoModificationConfig;
    private CloudConfig cloudConfig;
    private MixedConfig mixedConfig;
    private Map<String, Object> payloadConfig;

    public static Config getDefault() {
        return new Config(
                HttpTrafficAutoModificationConfig.getDefault(),
                CloudConfig.getDefault(),
                MixedConfig.getDefault(),
                new HashMap<>());
    }
}
