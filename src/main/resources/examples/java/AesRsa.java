import org.m2sec.core.utils.*;
import org.m2sec.core.models.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * 按 Ctrl（command） + ` 可查看内置函数
 */
public class AesRsa {

    private static final String SYMMERTIC_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] aesSecret = "32byteslongsecretkeyforaes256!aa".getBytes();
    private static final String ASYMMETRIC_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String publicKey1Base64 =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7JoQAWLsovzHjaUMZg2lwO4LCuP97mitUc4chqRlQD3NgyCWLqEnYyM" +
            "+OJ7i6cyMuWLwGtMi29DoKLjpE/xRZR0OUk46PDCAtyDgIyejK7c7KlZTbiqb4PtiJNLZgg0UP62kLMycnpY/wg/R2G9g+7MiJWUV5SR" +
            "+Lhryv8CWezQIDAQAB";
    private static final String privateKey1Base64 = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALsmhABYuyi" +
        "/MeNpQxmDaXA7gsK4/3uaK1RzhyGpGVAPc2DIJYuoSdjIz44nuLpzIy5YvAa0yLb0OgouOkT/FFlHQ5STjo8MIC3IOAjJ6MrtzsqVlNuKpvg" +
        "+2Ik0tmCDRQ/raQszJyelj/CD9HYb2D7syIlZRXlJH4uGvK/wJZ7NAgMBAAECgYAhgbhRbZF4rp6Kdh6e00HN58G2BjQrl4MZeCOh" +
        "+aoABPwlwD/EnMk36GAMtfzjWNjcI+PqGXT0GI7JotQo5ThpoweXX/uoeGOW+UkYLA6a67lmxfoZsDtY2" +
        "+jnaWIs2c7Itz3ClRxo4tYwCoPNjtaBpMfPgZaYg2QN8/wLQPI66wJBAM0xpjb2OlLDs75lVxbm6v6Dx3YBS20GSqJqvf+14a" +
        "/k7mrZ3PmAHOfqTqKOwbVQJmLbeOpU+sUBpeLpILKOCLcCQQDpfSsDhdosC6qTL9XnF2jS49iws2RBKw5YjDkClwA6VMNj5uzL1Rl7" +
        "/AimLRMnB4BwrD95ksuOJsqNXW6wRGibAkAkk28PaQCodB38GFBX0r2ctJy/Wie5vV9caC6KAD" +
        "/EfMhK357WEpIUfN2beFrrGOhewsRg8NjqeQq60dd0PIEtAkBYAm03O7n8Bj26kzpejA1gCLBCEqyEf/U9XUWT+1UDp7Wqr32sa1vaxyp" +
        "/cNgaSxKX5eVbLwD5SRfqZ0B0wqRnAkATpUNiCqjQVS+OI5dwjoI1Rx3oI8pyKWOg3+QIHIRgL3pc8HLdZ2BkX4Vf6ANb4+noQnD/di1Mj" +
        "+0pUL8RhIJE";
    private static final String publicKey2Base64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrfqYv278oDvreohZeR" +
        "+UMiNSJC2FG4u8dSNC/hj88lw7eULQOiuUnsZ4eineeFOquXckjhkh1AJsd87+Nz1ZQB61dY3NmVR5Tk+2wH" +
        "/kNdiVCoRrbULs29Tms17IyrZZU9WQFQbBxC/g6n5zwp6ST/siGRfHAwVVbq+iccQfdpwIDAQAB";
    private static final String privateKey2Base64 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKt+pi/bvygO" +
        "+t6iFl5H5QyI1IkLYUbi7x1I0L+GPzyXDt5QtA6K5Sexnh6Kd54U6q5dySOGSHUAmx3zv43PVlAHrV1jc2ZVHlOT7bAf" +
        "+Q12JUKhGttQuzb1OazXsjKtllT1ZAVBsHEL+DqfnPCnpJP+yIZF8cDBVVur6JxxB92nAgMBAAECgYAghb2lcNKBGcooo2uDiLXe2SoZLT" +
        "/O7iVzk8YGtEJUzr7imUJ0SZHoo639U7wYjhXtaFrHMmWWTr2cAggvMAVJi5fZYYJLbYdc8O5QCKi6PzV2J2NxYyuABL5yarvy4Ji0twnDjlqBYqrjOsxJbeMv58CHLKqduIZuxppGGOoRQQJBANTV3JEg6xJdPXsF9ztOf03BNkvpibuUSNbTssTdzEtLMQW7zd5y1qTCwUbf+e2UsRIYPn5DwOlTu8SaE97Zz8ECQQDORm7szA0WL1OTYob0U1NSSFDn8Jg7FyX5md6ndL3KNTKBDBfe3hNpauLi01lTMbO3MoriOWsFiN++6dZAdwdnAkEAq6PcwN1/Ncwj7Lae7yEa4SXUF9w6yx+GrlkDbmhAfOginLEcES0jlLPLEtFFySeEtUb//uu9A24XmzF2nN2jAQJABgL7fJ89ymW6s9LtR/WdugotgXT7ms1D6BBZ8ttuJJSEUkp975rdSfc5gY7TTZ9nM3GfppQx0El66994xQwzBQJAct1HPeCVROxyEHNwsiRH9wqR5P4B59Mo1714R7ozsdTpVx8FWmqi+OQIJt+IizYgRyQ09qORAFei9AHeQtxKiw==";

    private static final byte[] publicKey1 = CodeUtil.b64decode(publicKey1Base64);
    private static final byte[] privateKey1 = CodeUtil.b64decode(privateKey1Base64);

    private static final byte[] publicKey2 = CodeUtil.b64decode(publicKey2Base64);
    private static final byte[] privateKey2 = CodeUtil.b64decode(privateKey2Base64);

    private static final String jsonKey1 = "data";
    private static final String jsonKey2 = "key";

    private Logger log;

    public AesRsa(Logger log) {
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
        // 获取用来解密的密钥，该密钥已使用publicKey1进行rsa加密
        byte[] encryptedKey = getKey(request.getContent());
        // 调用内置函数解密，拿到aes密钥
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
        // 调用内置函数加密回去，这里使用设置的aesSecret进行加密
        byte[] encryptedData = symmetricEncrypt(data, aesSecret);
        // 调用内置函数加密aesSecret
        byte[] encryptedKey = asymmetricEncrypt(aesSecret, publicKey1);
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
        // 获取用来解密的密钥，该密钥已使用publicKey2进行rsa加密
        byte[] encryptedKey = getKey(response.getContent());
        // 调用内置函数解密，拿到aes密钥
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
        // 调用内置函数加密回去，这里使用设置的aesSecret进行加密
        byte[] encryptedData = symmetricEncrypt(data, aesSecret);
        // 调用内置函数加密aesSecret
        byte[] encryptedKey = asymmetricEncrypt(aesSecret, publicKey2);
        // 将已加密的数据转换为Server可识别的格式
        byte[] body = toData(encryptedData, encryptedKey);
        // 更新body
        response.setContent(body);
        return response;
    }

    public byte[] asymmetricDecrypt(byte[] content, byte[] secret) {
        return CryptoUtil.rsaDecrypt(ASYMMETRIC_ALGORITHM, content, secret);
    }

    public byte[] asymmetricEncrypt(byte[] content, byte[] secret) {
        return CryptoUtil.rsaEncrypt(ASYMMETRIC_ALGORITHM, content, secret);
    }

    public byte[] symmetricDecrypt(byte[] content, byte[] secret) {
        return CryptoUtil.aesDecrypt(SYMMERTIC_ALGORITHM, content, secret, null);
    }

    public byte[] symmetricEncrypt(byte[] content, byte[] secret) {
        return CryptoUtil.aesEncrypt(SYMMERTIC_ALGORITHM, content, secret, null);
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
