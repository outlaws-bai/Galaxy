import com.googlecode.aviator.AviatorEvaluator;
import org.junit.jupiter.api.Test;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class TempTest {

    @Test
    public void test() {

        Object s=AviatorEvaluator.execute("1");
        System.out.println(s.getClass());
    }
}
