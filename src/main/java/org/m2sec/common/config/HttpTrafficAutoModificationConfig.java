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
 * @date: 2024/6/7 14:16
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HttpTrafficAutoModificationConfig {

    private HookConfig hookConfig;
    private DecorateConfig decorateConfig;
    private MatchConfig specialRuleMatchConfig;

    public static HttpTrafficAutoModificationConfig getDefault() {
        return new HttpTrafficAutoModificationConfig(
                HookConfig.getDefault(), DecorateConfig.getDefault(), MatchConfig.getDefault());
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class DecorateConfig {
        private String requestDecorate;
        private String responseDecorate;

        public static DecorateConfig getDefault() {
            return new DecorateConfig("", "");
        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class HookConfig {

        private boolean hookRequestToBurp;
        private boolean hookRequestToServer;
        private boolean hookResponseToBurp;
        private boolean hookResponseToClient;
        private String requestMatcher;
        private HttpHookService service;
        private String rpcConn;
        private String scriptPath;

        public static HookConfig getDefault() {
            return new HookConfig(
                    false,
                    false,
                    false,
                    false,
                    "",
                    HttpHookService.RPC,
                    "127.0.0.1:8443",
                    Constants.HTTP_HOOK_SCRIPT_FILE_PATH);
        }

        public boolean isStart() {
            return hookRequestToServer
                    || hookRequestToBurp
                    || hookResponseToBurp
                    || hookResponseToClient;
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

        public static MatchConfig getDefault() {
            return new MatchConfig(new HashMap<>(), new HashMap<>(), new HashMap<>());
        }
    }
}
