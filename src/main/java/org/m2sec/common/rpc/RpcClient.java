package org.m2sec.common.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.m2sec.rpc.HttpHookServiceGrpc;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class RpcClient {
    private final ManagedChannel channel;

    public final HttpHookServiceGrpc.HttpHookServiceBlockingStub blockingStub;

    public RpcClient(String rpcConn) {
        channel = ManagedChannelBuilder.forTarget(rpcConn).usePlaintext().build();
        blockingStub = HttpHookServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        channel.shutdown();
    }
}
