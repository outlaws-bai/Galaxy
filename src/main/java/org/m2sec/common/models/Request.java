package org.m2sec.common.models;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.google.protobuf.ByteString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.m2sec.common.Constants;
import org.m2sec.common.Tuple;
import org.m2sec.common.enums.HttpContentType;
import org.m2sec.common.enums.HttpMethod;
import org.m2sec.common.utils.HttpUtil;
import org.m2sec.rpc.HttpHook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class Request {
    private Target target;
    private String httpVersion;
    private String method;
    private String path;
    private String queryStr;
    private List<Header> headers;
    private byte[] content;

    public static Request of(HttpRequest request) {
        List<Header> headers = new ArrayList<>(request.headers().stream().map(Header::of).toList());
        Tuple<String, String> temp = HttpUtil.parseFullPath(request.path());
        return new Request(
                Target.of(request.httpService()),
                request.httpVersion(),
                request.method(),
                temp.getFirst(),
                temp.getSecond(),
                headers,
                request.body().getBytes());
    }

    public static Request of(HttpHook.Request request) {
        List<Header> headers =
                new ArrayList<>(request.getHeaderList().stream().map(Header::of).toList());
        return new Request(
                Target.of(request.getTarget()),
                request.getHttpVersion(),
                request.getMethod(),
                request.getPath(),
                request.getQueryStr(),
                headers,
                request.getContent().toByteArray());
    }

    public static Request of(byte[] data, Target target) {
        int index = 0;

        // 解析请求行
        int start = index;
        while (index < data.length && data[index] != '\r' && data[index] != '\n') {
            index++;
        }
        String requestLine = new String(data, start, index - start);
        String[] requestLineParts = requestLine.split(" ");
        String httpVersion = "HTTP/1.1"; // 默认为 HTTP/1.1
        String method = requestLineParts[0];
        String fullPath = requestLineParts[1];

        // 适应性调整：检查请求行是否包含版本号
        if (requestLineParts.length > 2) {
            httpVersion = requestLineParts[2];
        }

        index += 2; // 跳过换行符

        // 解析头部
        ArrayList<Header> headers = new ArrayList<>();
        while (index + 1 < data.length && data[index] != '\r' && data[index + 1] != '\n') {
            start = index;
            while (index < data.length && data[index] != '\r') {
                index++;
            }
            if (index + 1 < data.length) {
                String headerLine = new String(data, start, index - start);
                String[] headerParts = headerLine.split(": ");
                if (headerParts.length == 2)
                    headers.add(new Header(headerParts[0], headerParts[1]));
            }

            index += 2; // 跳过 '\r\n'
        }

        index += 2; // 跳过换行符

        // 解析内容
        byte[] content = null;
        if (index < data.length) {
            content = new byte[data.length - index];
            System.arraycopy(data, index, content, 0, content.length);
        }

        Tuple<String, String> temp = HttpUtil.parseFullPath(fullPath);

        return new Request(
                target, httpVersion, method, temp.getFirst(), temp.getSecond(), headers, content);
    }

    public byte[] toMessage() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // 处理请求行
        String fullPath = HttpUtil.toFullPath(path, queryStr);
        String requestLine =
                String.format(
                        "%s %s %s\r\n", method, !fullPath.isEmpty() ? fullPath : "/", httpVersion);
        // 处理请求头
        String requestHeader =
                headers.stream()
                        .map(x -> String.format("%s: %s", x.getName(), x.getValue()))
                        .collect(Collectors.joining("\r\n"));
        // write请求行
        result.writeBytes(requestLine.getBytes());
        // write请求头
        result.writeBytes(requestHeader.getBytes());
        // write换行符
        result.writeBytes("\r\n\r\n".getBytes());
        // write content
        result.writeBytes(content);
        return result.toByteArray();
    }

    public HttpRequest toBurp() {
        HttpService httpService = target.toBurp();
        byte[] requestMessage = this.toMessage();
        return HttpRequest.httpRequest(httpService, ByteArray.byteArray(requestMessage));
    }

    public HttpHook.Request toRpc() {
        List<HttpHook.Header> headers = this.headers.stream().map(Header::toRpc).toList();
        return HttpHook.Request.newBuilder()
                .setTarget(this.target.toRpc())
                .setHttpVersion(this.httpVersion)
                .setMethod(this.method)
                .setPath(this.path)
                .setQueryStr(this.queryStr)
                .addAllHeader(headers)
                .setContent(ByteString.copyFrom(this.content))
                .build();
    }

    public boolean hasHeader(String headerName) {
        for (Header header : this.headers) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                return true;
            }
        }
        return false;
    }

    public Header getHeader(String headerName) {
        for (Header header : this.headers) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                return header;
            }
        }
        return null;
    }

    public Request addHeader(String name, String value) {
        this.headers.add(new Header(name, value));
        return this;
    }

    public Request removeHeader(String name) {
        this.headers =
                new ArrayList<>(
                        this.headers.stream()
                                .filter(x -> !x.getName().equalsIgnoreCase(name))
                                .toList());
        return this;
    }

    public Request updateHeader(String name, String value) {
        this.removeHeader(name);
        return this.addHeader(name, value);
    }

    public Request normalize() {
        path = HttpUtil.normalizePath(path);
        return this;
    }

    public Request updateContentLength() {
        return this.updateHeader(
                Constants.HTTP_HEADER_CONTENT_LENGTH, String.valueOf(this.content.length));
    }

    public HttpContentType getContentType() {
        Header contentTypeHeader = getHeader(Constants.HTTP_HEADER_CONTENT_TYPE);
        if (contentTypeHeader != null) {
            return HttpContentType.of(contentTypeHeader.getValue());
        } else if (method.equals(HttpMethod.HEAD.toString())
                && method.equals(HttpMethod.GET.toString())
                && method.equals(HttpMethod.GET.toString())) {
            return HttpContentType.TEXT;
        } else {
            return HttpContentType.NON_BODY;
        }
    }
}
