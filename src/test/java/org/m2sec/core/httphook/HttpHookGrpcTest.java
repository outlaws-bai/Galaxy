package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Request;

import java.io.IOException;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:47
 * @description:
 */
@Slf4j
public class HttpHookGrpcTest {

    public static final int rpcPort = 8443;

    @Test
    public void testJavaGRpcService() {

        // 创建一个新的线程启动Java的Rpc Server
        new Thread(() -> {
            HttpHookGrpcServer grpcServer = new HttpHookGrpcServer(rpcPort);
            try {
                grpcServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        testStartedGrpcHooker();
    }

    @Test
    public void testStartedGrpcHooker() {
        GRpcHooker service = new GRpcHooker();
        service.init(rpcPort);
        Request request = Request.of("https://www.baidu.com/a/b/c", Method.POST);
        request.setContent("{\"data\": \"0gXNBPtsCJ903KCjvXD6rQEod3XJ69SFCpN8QHuRQPw=\"}".getBytes());
        log.info("raw: {}", request);
        Request request1 = service.hookRequestToBurp(request);
        log.info("decrypted request: {}", request1);
        Request request2 = service.hookRequestToServer(request1);
        log.info("encrypted request: {}", request2);
    }
}
