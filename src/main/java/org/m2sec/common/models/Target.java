package org.m2sec.common.models;

import burp.api.montoya.http.HttpService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.rpc.HttpHook;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Target {

    private boolean secure;
    private String host;
    private int port;

    public static Target of(HttpService httpService) {
        return new Target(httpService.secure(), httpService.host(), httpService.port());
    }

    public static Target of(HttpHook.Target target) {
        return new Target(target.getSecure(), target.getHost(), target.getPort());
    }

    public static Target of(String url) {
        try {
            URL inputUrlObj = new URL(url);
            return Target.of(inputUrlObj);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Target of(URL urlObj) {
        boolean secure = urlObj.getProtocol().equals("https");
        return new Target(
                secure,
                urlObj.getHost(),
                urlObj.getPort() != -1 ? urlObj.getPort() : (secure ? 443 : 80));
    }

    public HttpService toBurp() {
        return HttpService.httpService(this.getHost(), this.getPort(), this.isSecure());
    }

    public HttpHook.Target toRpc() {
        return HttpHook.Target.newBuilder()
                .setSecure(this.isSecure())
                .setHost(this.getHost())
                .setPort(this.getPort())
                .build();
    }

    public String getProtocol() {
        return secure ? "https" : "http";
    }

    /**
     * @return eg: {protocol}://{fullHost}
     */
    public String getUrl() {
        return getProtocol() + "://" + getFullHost();
    }

    public String getFullHost() {
        if ((secure && port == 443) || (!secure && port == 80)) {
            return host;
        } else {

            return host + ":" + port;
        }
    }
}
