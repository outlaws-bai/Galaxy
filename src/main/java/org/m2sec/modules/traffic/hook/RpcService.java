package org.m2sec.modules.traffic.hook;

import org.m2sec.GalaxyMain;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.rpc.RpcClient;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class RpcService extends AbstractHttpHookService {

    public RpcClient client;

    @Override
    public void init() {
        this.client =
            new RpcClient(GalaxyMain.config.getHttpTrafficAutoModificationConfig().getHookConfig().getRpcConn());
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
