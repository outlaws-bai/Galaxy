package org.m2sec.core.httphook;

import org.junit.jupiter.api.Test;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Request;
import java.io.IOException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpHookTest {
    @Test
    public void testJavaFileService() {
        JavaFileHooker service = new JavaFileHooker();
        service.init(".\\src\\test\\java\\AesCbc.java");
        Request request = Request.of("https://www.baidu.com/a/b/c", Method.POST);
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
            HttpHookGrpcServer grpcServer = new HttpHookGrpcServer(8443);
            try {
                grpcServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        GRpcHooker service = new GRpcHooker();
        service.init("127.0.0.1:8443");
        Request request = Request.of("https://www.baidu.com/a/b/c", Method.POST);
        request.setContent("{\"data\": \"0gXNBPtsCJ903KCjvXD6rQEod3XJ69SFCpN8QHuRQPw=\"}".getBytes());
        System.out.println(request);
        Request request1 = service.hookRequestToBurp(request);
        System.out.println(request1);
        Request request2 = service.hookRequestToServer(request1);
        System.out.println(request2);
    }

}
