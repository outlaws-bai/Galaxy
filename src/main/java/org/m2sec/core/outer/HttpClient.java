package org.m2sec.core.outer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Constants;
import org.m2sec.core.enums.Protocol;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 14:08
 * @description:
 */
@Slf4j
public class HttpClient {

    public Response send(Request request) {
        return send(request, 5, false, null);
    }

    /**
     * @param proxyConn 代理连接串
     * @return 响应对象
     */
    public Response send(Request request, String proxyConn) {
        return send(request, 5, false, proxyConn);
    }

    public Response send(Request request, boolean followRedirect) {
        return send(request, 5, followRedirect, null);
    }

    public Response send(Request request, int timeoutSeconds) {
        return send(request, 5, false, null);
    }

    public Response send(Request request, int timeoutSeconds, boolean followRedirect, String proxyConn) {
        // 构建URL
        String protocol = Protocol.of(request.isSecure()).toRaw();
        String url = protocol + "://" + request.getHost() + ":" + request.getPort() + request.getPath();
        if (!request.getQuery().isEmpty()) {
            url += "?" + request.getQuery().toRawString();
        }

        // 构建HttpRequest
        HttpRequest httpRequest = HttpUtil.createRequest(Method.valueOf(request.getMethod()), url)
            .keepAlive(true)
            .header(request.getHeaders())
            .body(request.getContent());

        // 还原ua
        httpRequest.header(
            Constants.HTTP_HEADER_USER_AGENT,
            request.getHeaders().getFirstIgnoreCase(Constants.HTTP_HEADER_USER_AGENT),
            true
        );
        httpRequest.timeout(timeoutSeconds * 1000); // 超时时间单位为毫秒
        httpRequest.setFollowRedirects(followRedirect);

        // 设置代理
        if (proxyConn != null && !proxyConn.isEmpty()) {
            String proxyType = Protocol.HTTP.name();
            String proxyHost;
            int proxyPort;
            if (proxyConn.contains("://")) {
                String[] parts = proxyConn.split("://");
                assert parts.length == 2;
                proxyType = parts[0].toUpperCase();
                proxyConn = parts[1];
            }
            String[] parts = proxyConn.split(":");
            assert parts.length == 2;
            proxyHost = parts[0];
            proxyPort = Integer.parseInt(parts[1]);
            Proxy proxy = new Proxy(Proxy.Type.valueOf(proxyType), new InetSocketAddress(proxyHost, proxyPort));
            httpRequest.setProxy(proxy);
        }

        // 发送请求
        try (HttpResponse httpResponse = httpRequest.execute()) {
            int statusCode = httpResponse.getStatus();
            Headers responseHeaders = new Headers(httpResponse.headers());
            String reason = "OK";
            if (httpResponse.headers().containsKey(null)) {
                responseHeaders.remove(null);
                String responseLine = httpResponse.headers().get(null).get(0);
                String[] parts = responseLine.split(" ", 3);
                if (parts.length > 2) {
                    reason = parts[2];
                }
            }
            Response response = new Response("HTTP/1.1", statusCode, reason, responseHeaders, httpResponse.bodyBytes());
            log.info("send request {} - [{}].", request.getUrl(), statusCode);
            log.debug("request: {}", request);
            return response;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            throw e;
        }

    }
}
