package org.m2sec.common.config;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
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
    private BypassConfig bypassConfig;
    private FuzzConfig fuzzConfig;
    private Map<String, Object> payloadConfig;
}
