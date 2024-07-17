import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.m2sec.core.common.Helper;
import org.m2sec.core.common.ReflectTools;
import org.m2sec.core.common.Render;
import org.m2sec.core.models.Request;
import org.m2sec.core.utils.JsonUtil;

import javax.script.ScriptEngineManager;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:06
 * @description:
 */
@Slf4j
public class TempTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void test() throws Exception {
    }

    @Test
    public void test2() {
    }

    public static void main(String[] args) {
    }
}
