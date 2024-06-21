package org.m2sec.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.common.Constants;
import org.m2sec.common.enums.HttpHookService;

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
public class HttpTrafficAutoModificationConfig {

    private HookConfig hookConfig;
    private DecorateConfig decorateConfig;
    private MatchConfig ruleMatchConfig;


    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class DecorateConfig {
        private String requestModifyExpression;
        private String responseModifyExpression;

    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class HookConfig {

        private boolean requestIsNeedHook;
        private boolean responseIsNeedHook;
        private String requestMatchExpression;
        private HttpHookService hookService;
        private String rpcConn;
        private String javaFilePath;


        public boolean isStart() {
            return requestIsNeedHook || responseIsNeedHook;
        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class MatchConfig {
        private Map<String, Integer> requestParamMatches;
        private Map<String, Integer> responseHeaderMatches;
        private Map<String, Integer> responseContentMatches;

    }
}
