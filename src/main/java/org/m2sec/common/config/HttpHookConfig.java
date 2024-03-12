package org.m2sec.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.common.rpc.RpcClient;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HttpHookConfig {

    private boolean isHookRequestToServer;
    private boolean isHookRequestToBurp;
    private boolean isHookResponseToBurp;
    private boolean isHookResponseToClient;
    private String requestMatcher;
    private String responseMatcher;
    private RpcConfig rpc;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class RpcConfig {
        private String host;
        private int port;

        public static RpcConfig getDefault() {
            return new RpcConfig("127.0.0.1", 8443);
        }
    }

    public static HttpHookConfig getDefault() {
        return new HttpHookConfig(
                false, false, false, false, "true", "true", RpcConfig.getDefault());
    }

    public boolean isStart() {
        return isHookRequestToBurp
                || isHookRequestToServer
                || isHookResponseToBurp
                || isHookResponseToClient;
    }

    public RpcClient getRpcClient() {
        return new RpcClient(rpc.host, rpc.port);
    }
}
