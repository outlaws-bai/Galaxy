import org.m2sec.Galaxy;
import org.m2sec.core.common.Config;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 1:25
 * @description:
 */

public class TestSwing {
    public static void main(String[] args) {
        // 创建主窗体
        JFrame frame = new JFrame("Java Syntax Highlighting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        Config config = Config.ofWorkDir();
        frame.add(Galaxy.getMainPanel(config, null));

        // 窗体可见
        frame.setVisible(true);
    }

}
