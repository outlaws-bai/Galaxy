package org.m2sec.core.httphook;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.m2sec.rpc.HttpHookServiceGrpc;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class GrpcClient {
    private final ManagedChannel channel;

    public final HttpHookServiceGrpc.HttpHookServiceBlockingStub blockingStub;

    public GrpcClient(String rpcConn) {
        channel = ManagedChannelBuilder.forTarget(rpcConn).usePlaintext().build();
        blockingStub = HttpHookServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        channel.shutdown();
    }
}
