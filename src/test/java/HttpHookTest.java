import org.junit.jupiter.api.Test;
import org.m2sec.common.enums.Method;
import org.m2sec.common.models.Request;
import org.m2sec.modules.traffic.hook.JavaFileService;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpHookTest {
    @Test
    public void testJavaFile() {
        JavaFileService service = new JavaFileService();
        service.init(".\\src\\test\\java\\Hook.java");
        Request request = Request.of("https://www.baidu,com", Method.POST);
        request.setContent("{\"data\": \"0gXNBPtsCJ903KCjvXD6rQEod3XJ69SFCpN8QHuRQPw=\"}".getBytes());
        System.out.println(new String(request.toRaw()));
        service.hookRequestToBurp(request);
        System.out.println(new String(request.toRaw()));
        service.hookRequestToServer(request);
        System.out.println(new String(request.toRaw()));
    }

}
