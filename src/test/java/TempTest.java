import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.m2sec.core.common.Helper;
import java.security.*;

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
        Helper.initAndLoadConfig(null);
    }

    @Test
    public void test2() {
    }

    public static void main(String[] args) {
    }
}
