import org.m2sec.core.utils.*;
import org.m2sec.core.models.*;

import java.util.HashMap;
import java.util.Map;

import org.m2sec.shaded.slf4j.Logger;

/**
 * 按 Ctrl（command） + ` 可查看内置函数
 */
public class AesCbcForm {

    private Logger log;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] secret = "32byteslongsecretkeyforaes256!aa".getBytes();
    private static final byte[] iv = "16byteslongiv456".getBytes();
    private static final Map<String, Object> paramMap = new HashMap<>(Map.of("iv", iv));
    private static final String jsonKey = "data";

    public AesCbcForm(Logger log) {
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
        byte[] encryptedData = CodeUtil.b64decode(request.getForm().getFirst("username"));
        // 调用函数解密
        byte[] data = decrypt(encryptedData);
        // 更新Form
        Form newForm = new Form();
        newForm.put("username", new String(data));
        request.setForm(newForm);
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
        byte[] data = request.getForm().getFirst("username").getBytes();
        // 调用函数加密回去
        byte[] encryptedData = encrypt(data);
        // 更新Form
        Form newForm = new Form();
        newForm.put("username", CodeUtil.b64encodeToString(encryptedData));
        request.setForm(newForm);
        return request;
    }

    /**
     * HTTP响应从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
     *
     * @param response Response 响应对象
     * @return 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
     */
    public Response hookResponseToBurp(Response response) {
        // 获取需要解密的数据
        byte[] encryptedData = getData(response.getContent());
        // 调用函数解密
        byte[] data = decrypt(encryptedData);
        // 更新body
        response.setContent(data);
        return response;
    }

    /**
     * HTTP响应从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。
     *
     * @param response Response 响应对象
     * @return 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
     */
    public Response hookResponseToClient(Response response) {
        // 获取被解密的数据
        byte[] data = response.getContent();
        // 调用函数加密回去
        byte[] encryptedData = encrypt(data);
        // 更新body
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData);
        // 更新body
        response.setContent(body);
        return response;
    }

    public byte[] decrypt(byte[] content) {
        return CryptoUtil.aesDecrypt(ALGORITHM, content, secret, paramMap);
    }

    public byte[] encrypt(byte[] content) {
        return CryptoUtil.aesEncrypt(ALGORITHM, content, secret, paramMap);
    }

    public byte[] getData(byte[] content) {
        return CodeUtil.b64decode((String) JsonUtil.jsonStrToMap(new String(content)).get(jsonKey));
    }

    public byte[] toData(byte[] content) {
        HashMap<String, Object> jsonBody = new HashMap<>();
        jsonBody.put(jsonKey, CodeUtil.b64encodeToString(content));
        return JsonUtil.toJsonStr(jsonBody).getBytes();
    }
}
