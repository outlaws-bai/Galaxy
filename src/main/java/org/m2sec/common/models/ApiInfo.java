package org.m2sec.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.m2sec.common.Constants;
import org.m2sec.common.enums.ContentType;
import org.m2sec.common.enums.Method;
import org.m2sec.common.parsers.JsonParser;
import org.m2sec.common.utils.HttpUtil;

import java.net.URL;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description: all common type: string, number, integer, boolean, array, object, date, dateTime,
 * file; all position: path, query, header, cookie, formData, body
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class ApiInfo {
    private Version version;
    private Method method;
    private String path;
    private ContentType contentType;
    private Query query;
    private Headers headers;
    private Cookies cookies;
    private Form form;
    private FormDatas<String> formDatas;
    private FormDatas<UploadFile> files;
    private Map<String, Object> requestBody;
    private List<Object> requestBody2;
    private LinkedHashMap<String, String> notes;

    public ApiInfo(Version version, Method method, String path, ContentType contentType) {
        this.version = version;
        this.method = method;
        this.path = path;
        this.contentType = contentType;
        this.query = new Query();
        this.headers = new Headers();
        this.cookies = new Cookies();
        this.form = new Form();
        this.formDatas = new FormDatas<>();
        this.files = new FormDatas<>();
        this.requestBody = new HashMap<>();
        this.requestBody2 = new ArrayList<>();
        this.notes = new LinkedHashMap<>();
    }

    public enum Version {
        V2, V3
    }

    public Request generateRequest(Request request, String input) {
        boolean secure;
        String host;
        int port;
        String newPath;
        byte[] content;

        if (!input.startsWith("http")) {
            newPath = HttpUtil.normalizePath(input + path);
            secure = request.isSecure();
            host = request.getHost();
            port = request.getPort();

        } else {
            URL inputUrlObj = HttpUtil.parseUrl(input);
            secure = HttpUtil.urlIsSecure(inputUrlObj);
            host = inputUrlObj.getHost();
            port = HttpUtil.getUrlPort(inputUrlObj);
            newPath = HttpUtil.normalizePath(inputUrlObj.getPath() + "/" + path);
        }
        if (contentType == ContentType.JSON) {
            headers.put(Constants.HTTP_HEADER_CONTENT_TYPE, ContentType.JSON.toString());
            if (!requestBody.isEmpty()) {
                content = JsonParser.toJsonStr(requestBody).getBytes();
            } else if (!requestBody2.isEmpty()) {
                content = JsonParser.toJsonStr(requestBody2).getBytes();
            } else {
                content = "{}".getBytes();
            }
        } else if (contentType == ContentType.FORM) {
            headers.put(Constants.HTTP_HEADER_CONTENT_TYPE, ContentType.FORM.toString());
            content = form.toRawString().getBytes();
        } else if (contentType == ContentType.FORM_DATA) {
            String boundary = HttpUtil.generateBoundary();
            headers.put(Constants.HTTP_HEADER_CONTENT_TYPE, ContentType.FORM_DATA + "; boundary=" + boundary);
            content = HttpUtil.generateContentFormDataContent(boundary, formDatas, files);
        } else {
            // 其余暂时处理为空
            content = new byte[]{};
        }
        if (!cookies.isEmpty()) {
            headers.put("cookie", cookies.toRawString());
        }
        return new Request(secure, host, port, request.getVersion(), method.toString(), newPath, query, headers,
            content);
    }

    public String getNoteString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : this.notes.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }
}
