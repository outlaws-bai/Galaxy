import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.m2sec.common.Render;
import org.m2sec.common.models.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class TempTest {


    @Test
    public void test() throws IOException {

        Request request = Request.of("http://192.168.1.4:8000/getUserInfo");
        log.info("{}","192.168.1.4".equals(request.getHost()));
        Render.renderExpression("request.getHeaders().put(\"X-Request-Id\",java.util.UUID" +
            ".randomUUID().toString())", new HashMap<>(Map.of("request", request)));
        log.info("{}",request);
    }
}
