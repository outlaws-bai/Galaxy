package org.m2sec.modules.traffic.hook;

import com.google.protobuf.ByteString;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import org.m2sec.common.crypto.CryptoUtil;
import org.m2sec.common.parsers.JsonParser;
import org.m2sec.rpc.HttpHook;
import org.m2sec.rpc.HttpHookServiceGrpc;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpHookRpcServer {
    private Server server;
    private final int port;

    // 创建线程池
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public HttpHookRpcServer(int port) {
        this.port = port;
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws Exception {

        // The port on which the server should run
        int port = 8443; // default
        final HttpHookRpcServer server = new HttpHookRpcServer(port);
        server.start();
        server.blockUntilShutdown();
    }

    public void start() throws IOException {
        System.err.println("*** running");
        server =
            Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()).executor(executor).addService(new RpcServiceImpl()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its
            // JVM
            // shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            HttpHookRpcServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) server.shutdown();
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) server.awaitTermination();
    }


    static class RpcServiceImpl extends HttpHookServiceGrpc.HttpHookServiceImplBase {


        private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
        private static final byte[] secret = "32byteslongsecretkeyforaes256!aa".getBytes();
        private static final byte[] iv = "16byteslongiv456".getBytes();
        private static final Map<String, Object> paramMap = new HashMap<>(Map.of("iv", iv));


        private static byte[] getData(byte[] content) {
            return Base64.getDecoder().decode((String) JsonParser.jsonStrToMap(new String(content)).get("data"));
        }

        private static byte[] toData(byte[] content) {
            HashMap<String, Object> jsonBody = new HashMap<>();
            jsonBody.put("data", Base64.getEncoder().encodeToString(content));
            return JsonParser.toJsonStr(jsonBody).getBytes();
        }


        /**
         * 该函数在客户端请求到达Burp时被调用
         *
         * @param request          ...
         * @param responseObserver ...
         */
        @Override
        public void hookRequestToBurp(HttpHook.Request request, StreamObserver<HttpHook.Request> responseObserver) {
            // 获取需要解密的数据
            byte[] encryptedData = getData(request.getContent().toByteArray());
            // 调用内置函数解密
            byte[] data = CryptoUtil.aesDecrypt(ALGORITHM, encryptedData, secret, paramMap);
            // 更新body为已加密的数据
            responseObserver.onNext(request.toBuilder().setContent(ByteString.copyFrom(data)).build());
            responseObserver.onCompleted();
        }

        /**
         * 该函数在请求从Burp发送到服务端时被调用
         *
         * @param request          ...
         * @param responseObserver ...
         */
        @Override
        public void hookRequestToServer(HttpHook.Request request, StreamObserver<HttpHook.Request> responseObserver) {
            // 获取被解密的数据
            byte[] data = request.getContent().toByteArray();
            // 调用内置函数加密回去
            byte[] encryptedData = CryptoUtil.aesEncrypt(ALGORITHM, data, secret, paramMap);
            // 将已加密的数据转换为Server可识别的格式
            byte[] body = toData(encryptedData);
            // 更新body
            responseObserver.onNext(request.toBuilder().setContent(ByteString.copyFrom(body)).build());
            responseObserver.onCompleted();
        }

        /**
         * 该函数在响应从服务端刚到达Burp时被调用
         *
         * @param request          ...
         * @param responseObserver ...
         */
        @Override
        public void hookResponseToBurp(HttpHook.Response request, StreamObserver<HttpHook.Response> responseObserver) {
            // 获取需要解密的数据
            byte[] encryptedData = getData(request.getContent().toByteArray());
            // 调用内置函数解密
            byte[] data = CryptoUtil.aesDecrypt(ALGORITHM, encryptedData, secret, paramMap);
            // 更新body
            responseObserver.onNext(request.toBuilder().setContent(ByteString.copyFrom(data)).build());
            responseObserver.onCompleted();
        }

        /**
         * 该函数在响应从Burp发送到客户端时被调用
         *
         * @param request          ...
         * @param responseObserver ...
         */
        @Override
        public void hookResponseToClient(HttpHook.Response request,
                                         StreamObserver<HttpHook.Response> responseObserver) {
            // 获取被解密的数据
            byte[] data = request.getContent().toByteArray();
            // 调用内置函数加密回去
            byte[] encryptedData = CryptoUtil.aesEncrypt(ALGORITHM, data, secret, paramMap);
            // 更新body
            // 将已加密的数据转换为Server可识别的格式
            byte[] body = toData(encryptedData);
            // 更新body
            responseObserver.onNext(request.toBuilder().setContent(ByteString.copyFrom(body)).build());
            responseObserver.onCompleted();
        }
    }
}
