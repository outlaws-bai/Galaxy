package org.m2sec.common.rpc;

import io.grpc.stub.StreamObserver;
import org.m2sec.rpc.HttpHook;
import org.m2sec.rpc.HttpHookServiceGrpc;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class RpcServiceImpl extends HttpHookServiceGrpc.HttpHookServiceImplBase {


    /**
     * 该函数在客户端请求到达Burp时被调用
     *
     * @param request          ...
     * @param responseObserver ...
     */
    @Override
    public void hookRequestToBurp(HttpHook.Request request, StreamObserver<HttpHook.Request> responseObserver) {
        responseObserver.onNext(request);
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
        responseObserver.onNext(request);
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
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    /**
     * 该函数在响应从Burp发送到客户端时被调用
     *
     * @param request          ...
     * @param responseObserver ...
     */
    @Override
    public void hookResponseToClient(HttpHook.Response request, StreamObserver<HttpHook.Response> responseObserver) {
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }
}
