import org.m2sec.core.utils.*;
import org.m2sec.core.models.*;
import javax.annotation.Nullable;

/**
 * The available classes are as follows...
 * models：可能用到的DataObject
 * https://github.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/models
 * utils：可能用到的工具类
 * https://github.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/utils
 */
public class ${filename} {


    /**
     * HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。
     *
     * @param request org.m2sec.common.models.Request 请求对象
     * @return 经过处理后的request对象，返回null代表不需要处理
     */
    @Nullable
    public static Request hookRequestToBurp(Request request) {
        return null;
    }

    /**
     * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
     *
     * @param request org.m2sec.common.models.Request 请求对象
     * @return 经过处理后的request对象，返回null代表不需要处理
     */
    @Nullable
    public static Request hookRequestToServer(Request request) {
        return null;
    }

    /**
     * HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
     *
     * @param response org.m2sec.common.models.Response 响应对象
     * @return 经过处理后的response对象，返回null代表不需要处理
     */
    @Nullable
    public static Response hookResponseToBurp(Response response) {
        return null;
    }

    /**
     * HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。
     *
     * @param response org.m2sec.common.models.Response 响应对象
     * @return 经过处理后的response对象，返回null代表不需要处理
     */
    @Nullable
    public static Response hookResponseToClient(Response response) {
        return null;
    }
}