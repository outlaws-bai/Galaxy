// Utils: https://github1s.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/utils
import org.m2sec.core.utils.*;
// DataObject: https://github1s.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/models
import org.m2sec.core.models.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 限制：必须使用JDK启动Burp，JRE无法动态编译.java
 * 用法：使用Java代码修改请求/响应对象的任何部分以满足需求。
 * 内置：该项目内置了一些可能使用的DataObjects和Utils类，可以在代码中使用它们来满足加密、签名等要求。
 * 警告(*)：你应该使用Java代码调用项目中的内置Utils或DataObjects，不要尝试安装其他依赖项，这可能会导致兼容性问题。
 */
public class AesGcm{

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final byte[] secret = "32byteslongsecretkeyforaes256!aa".getBytes();
    private static final byte[] iv = "16byteslongiv456".getBytes();
    private static final Map<String, Object> paramMap = new HashMap<>(Map.of("iv", iv, "tLen", 128));
    private static final String jsonKey = "data";

    private Logger log;

    public AesGcm(Logger log) {
        this.log = log;
    }

    /**
     * HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。
     *
     * @param request Request 请求对象
     * @return 经过处理后的request对象，返回null代表不需要处理
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
     * @return 经过处理后的request对象，返回null代表不需要处理
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
     * @return 经过处理后的response对象，返回null代表不需要处理
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
     * @return 经过处理后的response对象，返回null代表不需要处理
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
        return CryptoUtil.aesDecrypt(ALGORITHM, content, secret, paramMap);
    }

    /**
     * @param content byte[] 要加密的数据
     * @return 加密结果
     */
    public byte[] encrypt(byte[] content) {
        return CryptoUtil.aesEncrypt(ALGORITHM, content, secret, paramMap);
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
