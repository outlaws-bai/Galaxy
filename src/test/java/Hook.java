import org.m2sec.common.crypto.CryptoUtil;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.parsers.JsonParser;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/13 22:17
 * @description:
 */
public class Hook {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] secret = "32byteslongsecretkeyforaes256!aa".getBytes();
    private static final byte[] iv = "16byteslongiv456".getBytes();
    private static Map<String, Object> paramMap;

    static {
        paramMap = new HashMap<>();
        paramMap.put("iv", iv);
    }

    private static byte[] getData(byte[] content) {
        return Base64.getDecoder()
                .decode((String) JsonParser.jsonStrToMap(new String(content)).get("data"));
    }

    private static byte[] toData(byte[] content) {
        HashMap<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("data", Base64.getEncoder().encodeToString(content));
        return JsonParser.toJsonStr(jsonBody).getBytes();
    }

    public static Request hookRequestToBurp(Request request) {
        request.setContent(
                CryptoUtil.aesDecrypt(ALGORITHM, getData(request.getContent()), secret, paramMap));
        return request;
    }

    public static Request hookRequestToServer(Request request) {
        request.setContent(
                toData(CryptoUtil.aesEncrypt(ALGORITHM, request.getContent(), secret, paramMap)));
        return request;
    }

    public static Response hookResponseToBurp(Response response) {
        response.setContent(
                CryptoUtil.aesDecrypt(ALGORITHM, getData(response.getContent()), secret, paramMap));
        return response;
    }

    public static Response hookResponseToClient(Response response) {
        response.setContent(
                toData(CryptoUtil.aesEncrypt(ALGORITHM, response.getContent(), secret, paramMap)));
        return response;
    }
}
