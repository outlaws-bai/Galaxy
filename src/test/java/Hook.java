import org.m2sec.common.crypto.CryptoUtil;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.parsers.JsonParser;

import javax.annotation.Nullable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class Hook {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] secret = "32byteslongsecretkeyforaes256!aa".getBytes();
    private static final byte[] iv = "16byteslongiv456".getBytes();
    private static final Map<String, Object> paramMap = new HashMap<>(Map.of("iv", iv));

    private static byte[] getData(byte[] content) {
        return Base64.getDecoder().decode((String) JsonParser.jsonStrToMap(new String(content)).get("data"));
    }

    private static byte[] toData(byte[] content) {
        HashMap<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("data", Base64.getEncoder().encodeToString(content));
        return JsonParser.toJsonStr(jsonBody).getBytes();
    }

    /**
     * HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。
     *
     * @param request org.m2sec.common.models.Request 请求对象
     * @return 经过处理后的request对象，返回null代表不需要处理
     */
    @Nullable
    public static Request hookRequestToBurp(Request request) {
        // 获取需要解密的数据
        byte[] encryptedData = getData(request.getContent());
        // 调用内置函数解密
        byte[] data = CryptoUtil.aesDecrypt(ALGORITHM, encryptedData, secret, paramMap);
        // 更新body为已加密的数据
        request.setContent(data);
        return request;
    }

    /**
     * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
     *
     * @param request org.m2sec.common.models.Request 请求对象
     * @return 经过处理后的request对象，返回null代表不需要处理
     */
    @Nullable
    public static Request hookRequestToServer(Request request) {
        // 获取被解密的数据
        byte[] data = request.getContent();
        // 调用内置函数加密回去
        byte[] encryptedData = CryptoUtil.aesEncrypt(ALGORITHM, data, secret, paramMap);
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData);
        // 更新body
        request.setContent(body);
        return request;
    }

    /**
     * HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
     *
     * @param response org.m2sec.common.models.Response 响应对象
     * @return 经过处理后的response对象，返回null代表不需要处理
     */
    @Nullable
    public static Response hookResponseToBurp(Response response) {
        // 获取需要解密的数据
        byte[] encryptedData = getData(response.getContent());
        // 调用内置函数解密
        byte[] data = CryptoUtil.aesDecrypt(ALGORITHM, encryptedData, secret, paramMap);
        // 更新body
        response.setContent(data);
        return response;
    }

    /**
     * HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。
     *
     * @param response org.m2sec.common.models.Response 响应对象
     * @return 经过处理后的response对象，返回null代表不需要处理
     */
    @Nullable
    public static Response hookResponseToClient(Response response) {
        // 获取被解密的数据
        byte[] data = response.getContent();
        // 调用内置函数加密回去
        byte[] encryptedData = CryptoUtil.aesEncrypt(ALGORITHM, data, secret, paramMap);
        // 更新body
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData);
        // 更新body
        response.setContent(body);
        return response;
    }
}
