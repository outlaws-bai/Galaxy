package org.m2sec.core.httphook;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.GrpcTools;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.rpc.HttpHookServiceGrpc;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class GRpcHooker extends IHttpHooker {

    private ManagedChannel channel;

    public HttpHookServiceGrpc.HttpHookServiceBlockingStub blockingStub;


    @Override
    public void init(Config config1) {
        config = config1;
        option = config1.getOption();
        init(option.getGrpcConn());
    }

    public void init(String grpcConn) {
        channel = ManagedChannelBuilder.forTarget(grpcConn).usePlaintext().build();
        blockingStub = HttpHookServiceGrpc.newBlockingStub(channel);
        log.info("Start grpc client success. {}", grpcConn);
    }

    public void init(int port) {
        channel = ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
        log.info("Start grpc client success. {}", port);
    }


    @Override
    public void destroy() {
        this.channel.shutdown();
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        return GrpcTools.ofGrpc(blockingStub.hookRequestToBurp(GrpcTools.toGrpc(request)));
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return GrpcTools.ofGrpc(blockingStub.hookRequestToServer(GrpcTools.toGrpc(request)));
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return GrpcTools.ofGrpc(blockingStub.hookResponseToBurp(GrpcTools.toGrpc(response)));
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return GrpcTools.ofGrpc(blockingStub.hookResponseToClient(GrpcTools.toGrpc(response)));
    }
}
