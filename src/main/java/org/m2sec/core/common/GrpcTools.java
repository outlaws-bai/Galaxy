package org.m2sec.core.common;

import com.google.protobuf.ByteString;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Query;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.core.utils.HttpUtil;
import org.m2sec.rpc.HttpHook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/9/21 15:47
 * @description:
 */

public class GrpcTools {

    public static Request ofGrpc(HttpHook.Request request){
            String fullPath = request.getFullPath();
            Tuple<String, String> temp = HttpUtil.parseFullPath(fullPath);
            return new Request(request.getSecure(), request.getHost(), request.getPort(), request.getVersion(),
                request.getMethod(), temp.getFirst(), Query.of(temp.getSecond()),
                ofGrpc(request.getHeadersList()), request.getContent().toByteArray());
    }

    public static HttpHook.Request toGrpc(Request request){
        return HttpHook.Request.newBuilder()
            .setSecure(request.isSecure())
            .setHost(request.getHost())
            .setPort(request.getPort())
            .setVersion(request.getVersion())
            .setMethod(request.getMethod())
            .setFullPath(request.getFullPath())
            .addAllHeaders(toGrpc(request.getHeaders()))
            .setContent(ByteString.copyFrom(request.getContent())).build();

    }

    public static Headers ofGrpc(List<HttpHook.Header> rpcHeaders) {
        Headers retVal = new Headers();
        for (HttpHook.Header rpcHeader : rpcHeaders) {
            retVal.add(rpcHeader.getKey(), rpcHeader.getValue());
        }
        return retVal;
    }

    public static List<HttpHook.Header> toGrpc(Headers headers) {
        List<HttpHook.Header> retVal = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String part : entry.getValue()) {
                retVal.add(HttpHook.Header.newBuilder().setKey(entry.getKey()).setValue(part).build());
            }
        }
        return retVal;
    }

    public static Response ofGrpc(HttpHook.Response response) {
        return new Response(response.getVersion(), response.getStatusCode(), response.getReason(),
            ofGrpc(response.getHeadersList()), response.getContent().toByteArray());
    }

    public static HttpHook.Response toGrpc(Response response) {
        return HttpHook.Response.newBuilder()
            .setVersion(response.getVersion())
            .setStatusCode(response.getStatusCode())
            .setReason(response.getReason())
            .addAllHeaders(toGrpc(response.getHeaders()))
            .setContent(ByteString.copyFrom(response.getContent())).build();
    }


}
