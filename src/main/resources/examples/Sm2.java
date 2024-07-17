import org.m2sec.core.utils.*;
import org.m2sec.core.models.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 内置示例，需要自定义代码文件时查看该文档：https://github.com/outlaws-bai/Galaxy/blob/main/docs/Custom.md
 * 局限性：必须使用JDK启动Burp
 */
public class Sm2{

    private static final String publicKeyBase64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEJniVFlbjYdpZrWlnnWt" +
        "/Ac9QBqIamsDL1GU9EB42Q6rVd7ArRAxtr6Ae5Xb+sSd9hc5LpIAR6jQ05v28LO8eFQ==";
    private static final String privateKeyBase64 =
        "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgSBfDgcogzGLkd9lNXKnLjZvjniGORXKZWsU3ncGkcdKgCgYIKoEcz1UBgi2hRANCAAQmeJUWVuNh2lmtaWeda38Bz1AGohqawMvUZT0QHjZDqtV3sCtEDG2voB7ldv6xJ32FzkukgBHqNDTm/bws7x4V";

    private static final byte[] publicKey = CodeUtil.b64decode(publicKeyBase64);

    private static final byte[] privateKey = CodeUtil.b64decode(privateKeyBase64);

    private static final String jsonKey = "data";

    private Logger log;

    public Sm2(Logger log) {
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
        byte[] data = decrypt(encryptedData);
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
        byte[] encryptedData = encrypt(data);
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
        byte[] data = decrypt(encryptedData);
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
        byte[] encryptedData = encrypt(data);
        // 更新body
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData);
        // 更新body
        response.setContent(body);
        return response;
    }

    /**
     * @param content byte[] 要解密的数据
     * @return 解密结果
     */
    public byte[] decrypt(byte[] content) {
        return CryptoUtil.sm2Decrypt(content, privateKey);
    }

    /**
     * @param content byte[] 要加密的数据
     * @return 加密结果
     */
    public byte[] encrypt(byte[] content) {
        return CryptoUtil.sm2Encrypt(content, publicKey);
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
