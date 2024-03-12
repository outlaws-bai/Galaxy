package org.m2sec.common.models;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.google.protobuf.ByteString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@AllArgsConstructor
public class Response {
    private String httpVersion;
    private int statusCode;
    private String reason;
    private List<Header> headers;
    private byte[] content;

    public static Response of(HttpResponse response) {
        List<Header> headers =
                new ArrayList<>(response.headers().stream().map(Header::of).toList());
        return new Response(
                response.httpVersion(),
                response.statusCode(),
                response.reasonPhrase(),
                headers,
                response.body().getBytes());
    }

    public static Response of(HttpHook.Response response) {
        List<Header> headers =
                new ArrayList<>(response.getHeaderList().stream().map(Header::of).toList());
        return new Response(
                response.getHttpVersion(),
                response.getStatusCode(),
                response.getReason(),
                headers,
                response.getContent().toByteArray());
    }

    public static Response of(byte[] data) {
        int index = 0;

        // 解析状态行
        int start = index;
        while (index < data.length && data[index] != '\r' && data[index] != '\n') {
            index++;
        }
        String responseLine = new String(data, start, index - start);
        String[] responseLineParts = responseLine.split(" ");
        String httpVersion = responseLineParts[0];
        int statusCode = Integer.parseInt(responseLineParts[1]);
        String reason = (responseLineParts.length > 2) ? responseLineParts[2] : "";

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

        return new Response(httpVersion, statusCode, reason, headers, content);
    }

    public HttpResponse toBurp() {
        byte[] responseMessage = this.toMessage();
        return HttpResponse.httpResponse(ByteArray.byteArray(responseMessage));
    }

    public HttpHook.Response toGRpc() {
        List<HttpHook.Header> headers = this.headers.stream().map(Header::toRpc).toList();
        return HttpHook.Response.newBuilder()
                .setHttpVersion(this.httpVersion)
                .setStatusCode(this.statusCode)
                .setReason(this.reason)
                .addAllHeader(headers)
                .setContent(ByteString.copyFrom(this.content))
                .build();
    }

    public byte[] toMessage() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // 处理响应行
        String responseLine = String.format("%s %s %s\r\n", httpVersion, statusCode, reason);
        // 处理响应头
        String responseHeader =
                headers.stream()
                        .map(x -> String.format("%s: %s", x.getName(), x.getValue()))
                        .collect(Collectors.joining("\r\n"));
        // write响应行
        result.writeBytes(responseLine.getBytes());
        // write响应头
        result.writeBytes(responseHeader.getBytes());
        // write换行符
        result.writeBytes("\r\n\r\n".getBytes());
        // write content
        result.writeBytes(content);
        return result.toByteArray();
    }

    public boolean hasHeader(String headerName) {
        for (Header header : this.headers) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                return true;
            }
        }
        return false;
    }

    public Response addHeader(String name, String value) {
        this.headers.add(new Header(name, value));
        return this;
    }

    public Response removeHeader(String name) {
        this.headers =
                new ArrayList<>(
                        this.headers.stream().filter(x -> !x.getName().equals(name)).toList());
        return this;
    }

    public Response updateHeader(String name, String value) {
        this.removeHeader(name);
        this.addHeader(name, value);
        return this;
    }
}
