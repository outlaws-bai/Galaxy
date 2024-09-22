package org.m2sec.core.models;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Tuple;
import org.m2sec.core.enums.ContentType;
import org.m2sec.core.enums.Method;
import org.m2sec.core.enums.Protocol;
import org.m2sec.core.utils.HttpUtil;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@Setter
@Slf4j
@Accessors(chain = true)
public class Request {
    /**
     * is https?
     */
    public boolean secure;

    /**
     * 连接目标的host, 非headers中的
     */
    public String host;

    public int port;
    public String version;
    public String method;

    /**
     * 不包含query参数, 有时因为Burp的原因，path会带上domain url
     */
    public String path;

    public Query query;

    public Headers headers;

    public byte[] content;

    public Request(boolean secure, String host, int port, String version, String method, String path, Query query,
                   Headers headers, byte[] content) {
        if (!headers.containsKey(Constants.HTTP_HEADER_HOST)) {
            headers.put(Constants.HTTP_HEADER_HOST, HttpUtil.getFullHost(secure, host, port));
        }
        if (!headers.containsKey(Constants.HTTP_HEADER_USER_AGENT)) {
            headers.put(Constants.HTTP_HEADER_USER_AGENT, Constants.HTTP_DEFAULT_USER_AGENT);
        }
        this.secure = secure;
        this.host = host;
        this.port = port;
        this.version = version;
        this.method = method;
        this.path = path;
        this.query = query;
        this.headers = headers;
        this.content = content;
    }


    public static Request of(HttpRequest request) {
        return Request.of(request.toByteArray().getBytes(), request.httpService().secure(),
            request.httpService().host(), request.httpService().port());
    }

    @SuppressWarnings("DuplicatedCode")
    public static Request of(byte[] raw, boolean secure, String host, int port) {
        int index = 0;

        // 解析请求行
        int start = index;
        while (index < raw.length && raw[index] != '\r' && raw[index] != '\n') {
            index++;
        }
        String requestLine = new String(raw, start, index - start);
        String[] requestLineParts = requestLine.split(" ");
        String httpVersion = Constants.HTTP_DEFAULT_VERSION; // 默认为 HTTP/1.1
        String method = requestLineParts[0];
        String fullPath = requestLineParts[1];

        // 适应性调整：检查请求行是否包含版本号
        if (requestLineParts.length > 2) {
            httpVersion = requestLineParts[2];
        }

        index += 2; // 跳过换行符

        // 解析头部
        Headers headers = new Headers();
        while (index + 1 < raw.length && raw[index] != '\r' && raw[index + 1] != '\n') {
            start = index;
            while (index < raw.length && raw[index] != '\r') {
                index++;
            }
            if (index + 1 < raw.length) {
                String headerLine = new String(raw, start, index - start);
                String[] headerParts = headerLine.split(Constants.HTTP_HEADER_CONN);
                if (headerParts.length == 2) headers.add(headerParts[0], headerParts[1]);
            }

            index += 2; // 跳过 '\r\n'
        }

        index += 2; // 跳过换行符

        // 解析内容
        byte[] content;
        if (index < raw.length) {
            content = new byte[raw.length - index];
            System.arraycopy(raw, index, content, 0, content.length);
        } else {
            content = new byte[]{};
        }

        Tuple<String, String> tuple = HttpUtil.parseFullPath(fullPath);

        if (host == null) {
            host = headers.getFirstIgnoreCase(Constants.HTTP_HEADER_HOST);
            assert host != null;
        }

        if (port == 0) {
            port = HttpUtil.defaultPort(secure);
        }

        return new Request(secure, host, port, httpVersion, method, tuple.getFirst(), Query.of(tuple.getSecond()),
            headers, content);
    }

    public static Request of(byte[] raw) {
        return Request.of(raw, false, null, 0);
    }


    public static Request of(String str) {
        if (str.startsWith(Protocol.HTTP.toRaw())) return of(str, Method.GET);
        else return of(str.getBytes());
    }

    public static Request of(String urlStr, String method) {
        return of(urlStr, Method.valueOf(method));
    }

    public static Request of(String urlStr, Method method) {
        URL url = HttpUtil.parseUrl(urlStr);
        Headers headers = new Headers();
        byte[] content = new byte[]{};
        if (!method.equals(Method.GET)) {
            headers.put(Constants.HTTP_HEADER_CONTENT_TYPE, ContentType.JSON.getHeaderValuePrefix());
            headers.put(Constants.HTTP_HEADER_CONTENT_LENGTH, "2");
            content = "{}".getBytes();
        }
        return new Request(HttpUtil.urlIsSecure(url), url.getHost(), HttpUtil.getUrlPort(url),
            Constants.HTTP_DEFAULT_VERSION, method.name(), url.getPath(),
            Query.of(url.getQuery()), headers, content);
    }

    public byte[] toRaw() {
        ByteArrayOutputStream retVal = new ByteArrayOutputStream();
        // 处理请求行
        String fullPath = HttpUtil.getFullPath(path, query.toRawString());
        String requestLine = String.format("%s %s %s\r\n", method, !fullPath.isEmpty() ? fullPath : "/", version);
        // 处理请求头
        String requestHeader = headers.toRawString();
        // write请求行
        retVal.writeBytes(requestLine.getBytes());
        // write请求头
        retVal.writeBytes(requestHeader.getBytes());
        // write换行符
        retVal.writeBytes("\r\n\r\n".getBytes());
        // write content
        retVal.writeBytes(content);
        return retVal.toByteArray();
    }

    public HttpRequest toBurp() {
        updateContentLength();
        HttpService httpService = HttpService.httpService(host, port, secure);
        byte[] requestMessage = this.toRaw();
        return HttpRequest.httpRequest(httpService, ByteArray.byteArray(requestMessage));
    }


    public Request normalize() {
        path = HttpUtil.normalizePath(path);
        return this;
    }

    public ContentType getContentType() {
        String value = headers.getFirstIgnoreCase(Constants.HTTP_HEADER_CONTENT_TYPE);
        return HttpUtil.getContentType(method, value);
    }

    public Cookies getCookies() {
        String cookieHeader = headers.getFirst(Constants.HTTP_HEADER_COOKIE);
        if (cookieHeader == null) return null;
        else return Cookies.of(cookieHeader);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Request updateContentLength() {
        headers.replaceIgnoreCase(Constants.HTTP_HEADER_CONTENT_LENGTH, String.valueOf(this.content.length));
        return this;
    }

    public Protocol getProtocol() {
        return Protocol.of(secure);
    }

    public String getUrl() {
        return HttpUtil.getUrl(secure, host, port, path);
    }

    public String getFullUrl() {
        return HttpUtil.getFullUrl(secure, host, port, path, query);
    }

    public String getFullPath() {
        return HttpUtil.getFullPath(path, query);
    }

    public String getBody() {
        return new String(content);
    }

    public void setBody(String body) {
        content = body.getBytes();
    }

    public Form getForm() {
        return Form.of(getBody());
    }

    public void setForm(Form form) {
        setBody(form.toRawString());
    }

    public FormData<Object> getFormData() {
        String boundary = HttpUtil.extractBoundary(headers.getFirstIgnoreCase(Constants.HTTP_HEADER_CONTENT_TYPE));
        assert boundary!=null: "boundary is null";
        return HttpUtil.parseContentFormDataContent(content, boundary);
    }


    public Object getJson() {
        return HttpUtil.bodyToJson(getBody());
    }

    public boolean isStaticExtension() {
        return isStaticExtension(Constants.HTTP_STATIC_EXTENSIONS);
    }

    public boolean isStaticExtension(String... staticExtensions) {
        return isStaticExtension(true, staticExtensions);
    }

    public boolean isStaticExtension(boolean merge, String... staticExtensions) {
        Set<String> unionSet = new HashSet<>(Arrays.asList(staticExtensions));
        if (merge)
            unionSet.addAll(Arrays.asList(Constants.HTTP_STATIC_EXTENSIONS));
        return unionSet.stream().anyMatch(getPath()::endsWith);
    }

    @Override
    public String toString() {
        return new String(toRaw());
    }

}
