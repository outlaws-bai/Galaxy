package org.m2sec.common.models;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.google.protobuf.ByteString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.m2sec.common.Constants;
import org.m2sec.rpc.HttpHook;

import java.io.ByteArrayOutputStream;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class Response {
    private String version;
    private int statusCode;
    private String reason;
    private Headers headers;
    private byte[] content;

    public static Response of(HttpResponse response) {
        return new Response(response.httpVersion(), response.statusCode(), response.reasonPhrase(),
            Headers.of(response.headers()), response.body().getBytes());
    }

    public static Response of(HttpHook.Response response) {
        return new Response(response.getVersion(), response.getStatusCode(), response.getReason(),
            Headers.of(response.getHeadersMap()), response.getContent().toByteArray());
    }

    @SuppressWarnings("DuplicatedCode")
    public static Response of(byte[] raw) {
        int index = 0;

        // 解析状态行
        int start = index;
        while (index < raw.length && raw[index] != '\r' && raw[index] != '\n') {
            index++;
        }
        String responseLine = new String(raw, start, index - start);
        String[] responseLineParts = responseLine.split(" ");
        String httpVersion = responseLineParts[0];
        int statusCode = Integer.parseInt(responseLineParts[1]);
        String reason = (responseLineParts.length > 2) ? responseLineParts[2] : "";

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
                String[] headerParts = headerLine.split(": ");
                if (headerParts.length == 2) headers.add(headerParts[0], headerParts[1]);
            }

            index += 2; // 跳过 '\r\n'
        }

        index += 2; // 跳过换行符

        // 解析内容
        byte[] content = null;
        if (index < raw.length) {
            content = new byte[raw.length - index];
            System.arraycopy(raw, index, content, 0, content.length);
        }

        return new Response(httpVersion, statusCode, reason, headers, content);
    }

    public Response updateContentLength() {
        headers.put(Constants.HTTP_HEADER_CONTENT_LENGTH, String.valueOf(this.content.length));
        return this;
    }

    public HttpResponse toBurp() {
        return HttpResponse.httpResponse(ByteArray.byteArray(updateContentLength().toRaw()));
    }

    public HttpHook.Response toRpc() {
        return HttpHook.Response.newBuilder().setVersion(version).setStatusCode(statusCode).setReason(reason).putAllHeaders(headers.toRpc()).setContent(ByteString.copyFrom(content)).build();
    }

    public byte[] toRaw() {
        ByteArrayOutputStream retVal = new ByteArrayOutputStream();
        // 处理响应行
        String responseLine = String.format("%s %s %s\r\n", version, statusCode, reason);
        // 处理响应头
        String responseHeader = headers.toRaw();
        // write响应行
        retVal.writeBytes(responseLine.getBytes());
        // write响应头
        retVal.writeBytes(responseHeader.getBytes());
        // write换行符
        retVal.writeBytes("\r\n\r\n".getBytes());
        // write content
        retVal.writeBytes(content);
        return retVal.toByteArray();
    }
}
