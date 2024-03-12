package org.m2sec.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.m2sec.common.Constants;
import org.m2sec.common.enums.HttpContentType;
import org.m2sec.common.enums.HttpMethod;
import org.m2sec.common.parsers.JsonParser;
import org.m2sec.common.utils.HttpUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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
public class ApiInfo {
    private Version version;
    private HttpMethod method;
    private String path;
    private HttpContentType contentType;
    // 除json body外的所有参数
    // all common type: string, number, integer, boolean, array, object, date, dateTime, file
    private List<Param> params;
    private Map<String, Object> requestBody;

    private LinkedHashMap<String, String> notes;

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    @AllArgsConstructor
    public static class Param {
        private String name;
        // maybe: string, number, integer, boolean, date, dateTime, file
        private Object value;
        // all position:
        // path, query, header, cookie, formData, body
        private String position;
    }

    public enum Version {
        V2,
        V3
    }

    public Request mergeRequest(Request request, String basePath) {
        Target target;
        ArrayList<Header> headers = new ArrayList<>();
        Map<String, String> query = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> forms = new HashMap<>();
        Map<String, String> formDatas = new HashMap<>();
        String newPath;
        Map<String, Object> jsonBody = new HashMap<>();

        if (basePath.startsWith("/")) {
            newPath = HttpUtil.normalizePath(basePath + path);
            target = request.getTarget();

        } else {
            try {
                URL inputUrlObj = new URL(basePath);
                newPath = HttpUtil.normalizePath(inputUrlObj.getPath() + "/" + path);
                target = Target.of(inputUrlObj);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        for (Param param : params) {
            Object value = param.getValue();
            if ("query".equals(param.position)) {
                query.put(param.getName(), value.toString());
            } else if ("path".equals(param.position)) {
                newPath = newPath.replaceAll("\\{" + param.name + "}", value.toString());
            } else if ("header".equals(param.position)) {
                headers.add(new Header(param.getName(), value.toString()));
            } else if ("cookie".equals(param.position)) {
                cookies.put(param.getName(), value.toString());
            } else if ("formData".equals(param.position)) {
                formDatas.put(param.getName(), value.toString());
            } else { // form
                forms.put(param.getName(), value.toString());
            }
        }
        byte[] content;
        if (contentType == HttpContentType.JSON) {
            headers.add(
                    new Header(
                            Constants.HTTP_HEADER_CONTENT_TYPE, HttpContentType.JSON.toString()));
            content = JsonParser.toJsonStr(jsonBody).getBytes();
        } else if (contentType == HttpContentType.FORM) {
            headers.add(
                    new Header(
                            Constants.HTTP_HEADER_CONTENT_TYPE, HttpContentType.FORM.toString()));
            content = HttpUtil.mapToQueryStr(forms).getBytes();
        } else if (contentType == HttpContentType.FORM_DATA) {
            // form data暂不处理
            headers.add(
                    new Header(
                            Constants.HTTP_HEADER_CONTENT_TYPE,
                            HttpContentType.FORM_DATA.toString()));
            content = new byte[] {};
        } else {
            // get
            content = new byte[] {};
        }
        headers.add(new Header(Constants.HTTP_HEADER_USER_AGENT, Constants.DEFAULT_USER_AGENT));
        headers.add(new Header("host", target.getFullHost()));
        if (!cookies.isEmpty()) {
            headers.add(new Header("cookie", HttpUtil.convertMapToCookieStr(cookies)));
        }
        return new Request(
                target,
                request.getHttpVersion(),
                method.toString(),
                newPath,
                HttpUtil.mapToQueryStr(query),
                headers,
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
