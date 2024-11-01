import org.m2sec.core.utils.*;
import org.m2sec.core.models.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * 按 Ctrl（command） + ` 可查看内置函数
 */
public class Sm2Sm4 {

    private static final String SYMMERTIC_ALGORITHM = "SM4/ECB/PKCS5Padding";
    private static final byte[] sm4Secret = "16byteslongkey12".getBytes();

    private static final String ASYMMETRIC_ALGORITHM = "SM2";

    private static final String SM2_MODE = "c1c2c3";
    private static final String publicKey1Base64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEBv9Z+xbmSOH3W/V9UEpU1yUiJKNGh" +
        "/I8EiENTPYxX3GujsZyKhuEUzxloKCATcNaKWi7w/yK3PxGONM4xvMlIQ==";
    private static final String privateKey1Base64 =
        "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgWmIprZ5a6TsqRUgy32J+F22AYIKl+14P4qlw" +
            "/LPPCcagCgYIKoEcz1UBgi2hRANCAAQG/1n7FuZI4fdb9X1QSlTXJSIko0aH8jwSIQ1M9jFfca6OxnIqG4RTPGWgoIBNw1opaLvD/Irc" +
            "/EY40zjG8yUh";

    private static final String publicKey2Base64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE/1kmIjlOfsqG9hN4b" +
        "/O3hiSI91ErgVDeqB9YOgCFiUiFyPo32pCHh691zGnoAj0l/P132CyLgBeH6TUa/TrLUg==";
    private static final String privateKey2Base64 =
        "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgP8vW9tEh0dMP5gJNsol5Gyc6jvvgK1NRqOVg8VaLYVygCgYIKoEcz1UBgi2hRANCAAT/WSYiOU5+yob2E3hv87eGJIj3USuBUN6oH1g6AIWJSIXI+jfakIeHr3XMaegCPSX8/XfYLIuAF4fpNRr9OstS";

    private static final byte[] publicKey1 = CodeUtil.b64decode(publicKey1Base64);
    private static final byte[] privateKey1 = CodeUtil.b64decode(privateKey1Base64);

    private static final byte[] publicKey2 = CodeUtil.b64decode(publicKey2Base64);
    private static final byte[] privateKey2 = CodeUtil.b64decode(privateKey2Base64);

    private static final String jsonKey1 = "data";
    private static final String jsonKey2 = "key";

    private Logger log;

    public Sm2Sm4(Logger log) {
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
        // 获取用来解密的密钥，该密钥已使用publicKey1进行sm2加密
        byte[] encryptedKey = getKey(request.getContent());
        // 调用内置函数解密，拿到sm4密钥
        byte[] key = asymmetricDecrypt(encryptedKey, privateKey1);
        // 调用内置函数解密报文
        byte[] data = symmetricDecrypt(encryptedData, key);
        // 更新body为已解密的数据
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
        // 调用内置函数加密回去，这里使用设置的sm4Secret进行加密
        byte[] encryptedData = symmetricEncrypt(data, sm4Secret);
        // 调用内置函数加密sm4Secret
        byte[] encryptedKey = asymmetricEncrypt(sm4Secret, publicKey1);
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData, encryptedKey);
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
        // 获取用来解密的密钥，该密钥已使用publicKey2进行sm2加密
        byte[] encryptedKey = getKey(response.getContent());
        // 调用内置函数解密，拿到sm4密钥
        byte[] key = asymmetricDecrypt(encryptedKey, privateKey2);
        // 调用内置函数解密报文
        byte[] data = symmetricDecrypt(encryptedData, key);
        // 更新body为已解密的数据
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
        // 调用内置函数加密回去，这里使用设置的sm4Secret进行加密
        byte[] encryptedData = symmetricEncrypt(data, sm4Secret);
        // 调用内置函数加密sm4Secret
        byte[] encryptedKey = asymmetricEncrypt(sm4Secret, publicKey2);
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData, encryptedKey);
        // 更新body
        response.setContent(body);
        return response;
    }

    public byte[] asymmetricDecrypt(byte[] content, byte[] secret) {
        return CryptoUtil.sm2Decrypt(SM2_MODE, content, secret);
    }

    public byte[] asymmetricEncrypt(byte[] content, byte[] secret) {
        return CryptoUtil.sm2Encrypt(SM2_MODE, content, secret);
    }

    public byte[] symmetricDecrypt(byte[] content, byte[] secret) {
        return CryptoUtil.sm4Decrypt(SYMMERTIC_ALGORITHM, content, secret, null);
    }

    public byte[] symmetricEncrypt(byte[] content, byte[] secret) {
        return CryptoUtil.sm4Encrypt(SYMMERTIC_ALGORITHM, content, secret, null);
    }

    private byte[] getData(byte[] content) {
        return CodeUtil.b64decode((String) JsonUtil.jsonStrToMap(new String(content)).get(jsonKey1));
    }

    private byte[] getKey(byte[] content) {
        return CodeUtil.b64decode((String) JsonUtil.jsonStrToMap(new String(content)).get(jsonKey2));
    }

    private byte[] toData(byte[] content, byte[] secret) {
        HashMap<String, Object> jsonBody = new HashMap<>();
        jsonBody.put(jsonKey1, CodeUtil.b64encodeToString(content));
        jsonBody.put(jsonKey2, CodeUtil.b64encodeToString(secret));
        return JsonUtil.toJsonStr(jsonBody).getBytes();
    }
}
