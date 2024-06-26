package org.m2sec.modules.traffic.hook;

import org.junit.jupiter.api.Test;
import org.m2sec.common.enums.Method;
import org.m2sec.common.models.Request;

import java.io.IOException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpTrafficHookTest {
    @Test
    public void testJavaFileService() {
        JavaFileService service = new JavaFileService();
        service.init(".\\src\\test\\java\\Hook.java");
        Request request = Request.of("https://www.baidu.com", Method.POST);
        request.setContent("{\"data\": \"0gXNBPtsCJ903KCjvXD6rQEod3XJ69SFCpN8QHuRQPw=\"}".getBytes());
        System.out.println(request);
        Request request1 = service.hookRequestToBurp(request);
        System.out.println(request1);
        Request request2 = service.hookRequestToServer(request1);
        System.out.println(request2);
    }

    @Test
    public void testRpcService() {

        // 创建一个新的线程启动Java的Rpc Server
        new Thread(() -> {
            HttpHookRpcServer rpcServer = new HttpHookRpcServer(8443);
            try {
                rpcServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        RpcService service = new RpcService();
        service.init("127.0.0.1:8443");
        Request request = Request.of("https://www.baidu.com", Method.POST);
        request.setContent("{\"data\": \"0gXNBPtsCJ903KCjvXD6rQEod3XJ69SFCpN8QHuRQPw=\"}".getBytes());
        System.out.println(request);
        Request request1 = service.hookRequestToBurp(request);
        System.out.println(request1);
        Request request2 = service.hookRequestToServer(request1);
        System.out.println(request2);
    }

}
