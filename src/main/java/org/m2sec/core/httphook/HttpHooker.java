package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Query;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.core.outer.HttpClient;
import org.m2sec.core.utils.CodeUtil;
import org.m2sec.core.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/9/22 16:30
 * @description:
 */
@Slf4j
public class HttpHooker extends IHttpHooker {

    private String httpConn;

    @Override
    public void init(Config config1) {
        config = config1;
        option = config1.getOption();
        init(config1.getOption().getHttpServer());
    }

    public void init(String httpConn1) {
        httpConn = httpConn1;
        log.info("Start http client success. {}", httpConn);
    }

    @Override
    public void destroy() {

    }

    @Override
    public Request hookRequestToBurp(Request request) {
        return jsonToRequest(call(request, Constants.HOOK_FUNC_1));
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return jsonToRequest(call(request, Constants.HOOK_FUNC_2));
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return jsonToResponse(call(response, Constants.HOOK_FUNC_3));
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return jsonToResponse(call(response, Constants.HOOK_FUNC_4));
    }

    public Map<String, Object> call(Request request, String func) {
        Map<String, Object> requestJson = requestToJson(request);
        Request callRequest = Request.of(httpConn + "/" + func, Method.POST);
        callRequest.setContent(JsonUtil.toJsonStr(requestJson).getBytes());
        return (Map<String, Object>) HttpClient.send(callRequest).getJson();
    }

    public Map<String, Object> call(Response response, String func) {
        Map<String, Object> responseJson = responseToJson(response);
        Request callRequest = Request.of(httpConn + "/" + func, Method.POST);
        callRequest.setContent(JsonUtil.toJsonStr(responseJson).getBytes());
        return (Map<String, Object>) HttpClient.send(callRequest).getJson();
    }

    public Map<String, Object> requestToJson(Request request) {
        Map<String, Object> requestJson = new HashMap<>();
        requestJson.put("secure", request.isSecure());
        requestJson.put("host", request.getHost());
        requestJson.put("port", request.getPort());
        requestJson.put("version", request.getVersion());
        requestJson.put("method", request.getMethod());
        requestJson.put("path", request.getPath());
        requestJson.put("query", request.getQuery());
        requestJson.put("headers", request.getHeaders());
        requestJson.put("contentBase64", CodeUtil.b64encodeToString(request.getContent()));
        return requestJson;
    }

    public Request jsonToRequest(Map<String, Object> requestJson) {
        boolean secure = (boolean) requestJson.get("secure");
        String host = (String) requestJson.get("host");
        int port = ((Double) requestJson.get("port")).intValue();
        String version = (String) requestJson.get("version");
        String method = (String) requestJson.get("method");
        String path = (String) requestJson.get("path");
        Query query = JsonUtil.mapToObject((Map<?, ?>) requestJson.get("query"), Query.class);
        Headers headers = JsonUtil.mapToObject((Map<?, ?>) requestJson.get("headers"), Headers.class);
        byte[] content = CodeUtil.b64decode((String) requestJson.get("contentBase64"));
        return new Request(secure, host, port, version, method, path, query, headers, content);
    }

    public Map<String, Object> responseToJson(Response response) {
        Map<String, Object> requestJson = new HashMap<>();
        requestJson.put("version", response.getVersion());
        requestJson.put("statusCode", response.getStatusCode());
        requestJson.put("reason", response.getReason());
        requestJson.put("headers", response.getHeaders());
        requestJson.put("contentBase64", CodeUtil.b64encodeToString(response.getContent()));
        return requestJson;
    }

    public Response jsonToResponse(Map<String, Object> responseJson) {
        String version = (String) responseJson.get("version");
        int statusCode = ((Double) responseJson.get("statusCode")).intValue();
        String reason = (String) responseJson.get("reason");
        Headers headers = JsonUtil.mapToObject((Map<?, ?>) responseJson.get("headers"), Headers.class);
        byte[] content = CodeUtil.b64decode((String) responseJson.get("contentBase64"));
        return new Response(version, statusCode, reason, headers, content);
    }
}
