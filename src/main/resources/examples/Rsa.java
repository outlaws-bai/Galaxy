import org.m2sec.core.utils.*;
import org.m2sec.core.models.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 内置示例，需要自定义代码文件时查看该文档：https://github.com/outlaws-bai/Galaxy/blob/main/docs/Custom.md
 * 局限性：必须使用JDK启动Burp
 */
public class Rsa{

    private static final String ALGORITHM = "RSA";
    private static final String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWlwW7eFn0apxrW0j" +
        "+W9fcGiJ9Pq8fDS7mGpF28kTz5mwbe5wajs7r9CQrcQS5mD75aItdNT" +
        "/XTPPCGawvgF4N4gxtExlzDNackg1YygGpVPuNY3B8M616vl6av0j0JyiWh9/KYG0oPgVvlpvIiT8a1OuOXNwX0f7LoIoBbXN0FMVVF4B" +
        "+/r9N22I2V9EgiaRVqYRC9tI5471FuSs6IKkh2TrLzssZ4D4ZAGC2bz6aejAmiYqWSQ/D5WcnDAg16KJUx1rmA57KctQjDM" +
        "+B7jYGg1MQYoEFdlUIaKDalr2uMoQWkK4ebLECdU67w5/E1KCp7/1+mGu/ijQxegJ5Z2qQIDAQAB";
    private static final String privateKeyBase64 =
        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVaXBbt4WfRqnGtbSP5b19waIn0" +
            "+rx8NLuYakXbyRPPmbBt7nBqOzuv0JCtxBLmYPvloi101P9dM88IZrC+AXg3iDG0TGXMM1pySDVjKAalU+41jcHwzrXq+Xpq" +
            "/SPQnKJaH38pgbSg+BW+Wm8iJPxrU645c3BfR/sugigFtc3QUxVUXgH7" +
            "+v03bYjZX0SCJpFWphEL20jnjvUW5KzogqSHZOsvOyxngPhkAYLZvPpp6MCaJipZJD8PlZycMCDXoolTHWuYDnspy1CMMz4HuNgaDUxBigQV2VQhooNqWva4yhBaQrh5ssQJ1TrvDn8TUoKnv/X6Ya7+KNDF6AnlnapAgMBAAECggEAI4lZ/36Fag4xCEGkigsvCC+bZVSqj1Pjn24b/SZioPRrFU0hAdYvUFOuK1cGKa0AK+aGapSyZ9i2B4vIlvHN2B+M9SzBWj/xw2TiidgyJlB4DzLoENEW+D/65ZqQBtjbjCINwR8uBTj3jUgyJXTolzVMwX5q80fS5YeT0JvOIWZp2q/+/PH9Awy3dAWmC7bMmIdOSsqD2hJKq4KtO41EI9AhkVCqVXJzzPwriFr1UBOaM3y9Gr1BumxCPGJIr9UD2paOk/lbCF2Vlui+iZjbtzlLYrs2zLuQpMtA8eGBYAlsXS4Q7CKnbNvQCCYLzsjqTU5uZEhAGnFN2mXJAjdsLwKBgQC5cqloKiBf4zQAiS3uIlEHcJy8fkatvvC8ofNQgsrGy/2YUYGaevVnY7I4e5cNypDvulek0idtmPGRkKI0XyAP+72TOYltZyo8hXt2/4kAVZkR0jfjvYIkWDLssIbCE/rx4fGZOVTxfZ3qQkGOfmra8q50FcDd0LRlf68s4ej0cwKBgQDOQR5EQhN5n/fTyv4umVDn253PpqiYbWNTItk9TRKfwIDwgFpgGQBfu/jYBeei3fJTe3TBj5PYoCQA6ETktg5YZv3MIu/DLEH5DCjLFso07ZNF5FWtbRRx5c+XVY6LXksRCnGWXIti420pdjIewyNtnrbt6NNPKXBdumn2eHn9cwKBgQCm96OrU3J+otPpP5mI9IC8EBouZAtC181sKOwnKvtjbbrP72KfMeHNyqdlz3C7TAyeqsnKbiRtuuyUwQIp4RO/EEspSP6A8AfJIe19wKkbEfaVYw5LEA8ipf6DuJQ8HT0tlt6ttD7UhuMtTaY0o4GVzDZh8kwJ6qThVcrkwCm8HQKBgAUbmbtJA6B0dLe7BDZ1N4q2Zp8Y2n4D33zUlRquiPKJ2ueZ1iMhG2BDkHMRGl5vLqwgl5CflKK9vIaFOgeL9qj7y/c9OkDUHMIlKfF1nAZZh9coQ3LrC6GSdmSiCsiqyiMe5hc6LX2Cclafhbg7TupNDuyvYmRIe27mye7/ps2/AoGARgDoTlQ9IfYAk62ZYBn8NmwZc9L7a2JVT4COQ3Y4K8bhJX4EOYsQGX5DSYXGiGjIG2lMPrQTnVgKr6a2tXKGsq9VEHlv2i7V1NjMFPKEzj8v7xSJvX4QDR1JmN4NLMBdD52KNzx6fx8kAHwoMJqhlspBCYyktwiRl7IkRRaGWEc=";

    private static final byte[] publicKey = CodeUtil.b64decode(publicKeyBase64);

    private static final byte[] privateKey = CodeUtil.b64decode(privateKeyBase64);

    private static final String jsonKey = "data";

    private Logger log;

    public Rsa(Logger log) {
        this.log = log;
    }

    /**
     * HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。
     *
     * @param request Request 请求对象
     * @return 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
     */
    public Request hookRequestToBurp(Request request) {
        // 获取需要解密的数据
        byte[] encryptedData = getData(request.getContent());
        // 调用内置函数解密
        byte[] data = CryptoUtil.rsaDecrypt(ALGORITHM, encryptedData, privateKey);
        // 更新body为已加密的数据
        request.setContent(data);
        return request;
    }

    /**
     * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
     *
     * @param request Request 请求对象
     * @return 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
     */
    public Request hookRequestToServer(Request request) {
        // 获取被解密的数据
        byte[] data = request.getContent();
        // 调用内置函数加密回去
        byte[] encryptedData = CryptoUtil.rsaEncrypt(ALGORITHM, data, publicKey);
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData);
        // 更新body
        request.setContent(body);
        return request;
    }

    /**
     * HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
     *
     * @param response Response 响应对象
     * @return 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
     */
    public Response hookResponseToBurp(Response response) {
        // 获取需要解密的数据
        byte[] encryptedData = getData(response.getContent());
        // 调用内置函数解密
        byte[] data = CryptoUtil.rsaDecrypt(ALGORITHM, encryptedData, privateKey);
        // 更新body
        response.setContent(data);
        return response;
    }

    /**
     * HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。
     *
     * @param response Response 响应对象
     * @return 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
     */
    public Response hookResponseToClient(Response response) {
        // 获取被解密的数据
        byte[] data = response.getContent();
        // 调用内置函数加密回去
        byte[] encryptedData = CryptoUtil.rsaEncrypt(ALGORITHM, data, publicKey);
        // 更新body
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData);
        // 更新body
        response.setContent(body);
        return response;
    }

    public byte[] decrypt(byte[] content) {
        return CryptoUtil.rsaDecrypt(ALGORITHM, content, privateKey);
    }

    public byte[] encrypt(byte[] content) {
        return CryptoUtil.rsaEncrypt(ALGORITHM, content, publicKey);
    }

    private static byte[] getData(byte[] content) {
        return CodeUtil.b64decode((String) JsonUtil.jsonStrToMap(new String(content)).get(jsonKey));
    }

    private static byte[] toData(byte[] content) {
        HashMap<String, Object> jsonBody = new HashMap<>();
        jsonBody.put(jsonKey, CodeUtil.b64encodeToString(content));
        return JsonUtil.toJsonStr(jsonBody).getBytes();
    }
}
