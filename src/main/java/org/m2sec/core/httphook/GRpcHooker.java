package org.m2sec.core.httphook;


import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Config;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class GRpcHooker extends IHttpHooker {

    public GrpcClient client;


    @Override
    public void init(Config config1) {
        config = config1;
        option = config1.getOption();
        init(option.getGrpcConn());
    }

    public void init(String grpcConn) {
        this.client = new GrpcClient(grpcConn);
        log.info("Start grpc client success. {}", grpcConn);
    }

    public void init(int port) {
        this.client = new GrpcClient(port);
        log.info("Start grpc client success. {}", port);
    }


    @Override
    public void destroy() {
        this.client.shutdown();
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        return Request.of(client.blockingStub.hookRequestToBurp(request.toRpc()));
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return Request.of(client.blockingStub.hookRequestToServer(request.toRpc()));
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return Response.of(client.blockingStub.hookResponseToBurp(response.toRpc()));
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return Response.of(client.blockingStub.hookResponseToClient(response.toRpc()));
    }
}
