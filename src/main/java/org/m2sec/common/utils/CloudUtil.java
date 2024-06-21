package org.m2sec.common.utils;

import burp.api.montoya.utilities.DigestAlgorithm;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.config.CloudConfig;
import org.m2sec.common.enums.ContentType;
import org.m2sec.common.enums.Method;
import org.m2sec.common.models.Headers;
import org.m2sec.common.models.Query;
import org.m2sec.common.models.Request;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class CloudUtil {

    private static final String AUTHORIZATION = "authorization";
    private static final String CONTENT_MD_5 = "content-md5";
    private static final String AWS_4_REQUEST = "aws4_request";

    private static final List<String> SIGNED_HEADERS_BLACKLIST = new ArrayList<>(List.of("expect", "user-agent",
        "connection"));

    private static final Log log = new Log(CloudUtil.class);

    public static void signAws(Request request, CloudConfig.AwsConfig awsConfig) {
        // remove aws headers
        Headers headers = request.getHeaders();
        Iterator<Map.Entry<String, List<String>>> headersIterator = headers.entrySet().iterator();
        while (headersIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = headersIterator.next();
            String key = entry.getKey();
            if (key.startsWith("amz") || key.startsWith("x-amz") || key.equals(AUTHORIZATION) || key.equals(CONTENT_MD_5)) {
                // 移除aws相关的请求头
                headersIterator.remove();
            }
        }
        // get date header value
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String amzDate = dateFormat.format(new Date());
        // calc content sha256 and md5
        String contentSha256 = HashUtil.calcHashToHex(request.getContent(), DigestAlgorithm.SHA_256);
        // calc authorization
        headers.add("x-amz-date", amzDate).add("x-amz-content-sha256", contentSha256);
        // add some header
        String method = request.getMethod();
        if (!(method.equals(Method.GET.name()) || method.equals(Method.OPTIONS.name()) || method.equals(Method.HEAD.name()))) {
            headers.put("content-md5", HashUtil.calcHashToBase64(request.getContent(), DigestAlgorithm.MD5));
            headers.put(Constants.HTTP_HEADER_CONTENT_TYPE, ContentType.FORM.getHeaderValuePrefix());
        }
        if (awsConfig.getToken() != null && !awsConfig.getToken().isBlank())
            headers.add("x-amz-security-token", awsConfig.getToken());
        String authorization = calcAwsAuthorization(awsConfig, amzDate, contentSha256, request.getQuery(),
            request.getHeaders(), request.getMethod(), request.getPath());
        headers.add("authorization", authorization);
    }

    private static String calcAwsAuthorization(CloudConfig.AwsConfig awsConfig, String amzDate, String contentSha256,
                                               Query query, Headers headers, String method, String path) {
        log.debug("amzDate: %s", amzDate);
        // 生成日期字符串
        String dayStamp = amzDate.substring(0, 8);
        // 处理query string
        List<String> queryParamNames = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : query.entrySet()) {
            for (String name : entry.getValue()) {
                queryParamNames.add(entry.getKey() + "=");
            }
        }
        Collections.sort(queryParamNames);
        String canonicalQueryString = String.join("&", queryParamNames);
        log.debug("canonicalQueryString: %s", canonicalQueryString);
        // 处理header
        ArrayList<String> headerFullStrings = new ArrayList<>();
        ArrayList<String> headerNames = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (!SIGNED_HEADERS_BLACKLIST.contains(entry.getKey())) {
                for (String value : entry.getValue()) {
                    headerNames.add(entry.getKey());
                    headerFullStrings.add(entry.getKey() + ":" + value.trim());
                }
            }
        }
        Collections.sort(headerFullStrings);
        Collections.sort(headerNames);
        String canonicalHeaderString = String.join("\n", headerFullStrings);
        String signedHeaderNames = String.join(";", headerNames);
        log.debug("canonicalHeaderString: %s", canonicalHeaderString);
        log.debug("signedHeaderNames: %s", signedHeaderNames);
        String canonicalRequest =
            method + "\n" + path + "\n" + canonicalQueryString + "\n" + canonicalHeaderString + "\n\n" + signedHeaderNames + "\n" + contentSha256;
        log.debug("canonicalRequest: %s", canonicalRequest);
        // calc signature
        String scopeString = amzDate.substring(0, 8) + "/" + awsConfig.getRegion() + "/" + awsConfig.getService() +
            "/" + AWS_4_REQUEST;
        String stringToSign =
            "AWS4-HMAC-SHA256\n" + amzDate + "\n" + scopeString + "\n" + HashUtil.calcHashToHex(canonicalRequest.getBytes(), DigestAlgorithm.SHA_256);
        log.debug("stringToSign: %s", stringToSign);
        byte[] kSecret = ("AWS4" + awsConfig.getSk()).getBytes();
        byte[] kDate = HashUtil.calcHmac(kSecret, DigestAlgorithm.SHA_256, dayStamp.getBytes());
        byte[] kRegion = HashUtil.calcHmac(kDate, DigestAlgorithm.SHA_256, awsConfig.getRegion().getBytes());
        byte[] kService = HashUtil.calcHmac(kRegion, DigestAlgorithm.SHA_256, awsConfig.getService().getBytes());
        byte[] kSignature = HashUtil.calcHmac(kService, DigestAlgorithm.SHA_256, AWS_4_REQUEST.getBytes());
        String signature = HashUtil.calcHmacToHex(kSignature, DigestAlgorithm.SHA_256, stringToSign.getBytes());
        // calc authorization
        return String.format("AWS4-HMAC-SHA256 Credential=%s, SignedHeaders=%s, Signature=%s", awsConfig.getAk() +
            "/" + scopeString, signedHeaderNames, signature);
    }
}
