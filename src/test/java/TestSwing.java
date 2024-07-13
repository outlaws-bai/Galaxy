import org.m2sec.core.common.Config;
import org.m2sec.core.common.Helper;
import org.m2sec.panels.MainPanel;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 1:25
 * @description:
 */

public class TestSwing {
    public static void main(String[] args) {
        Helper.initAndLoadConfig(null);
        // 创建主窗体
        JFrame frame = new JFrame("Java Syntax Highlighting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        Config config = Config.ofDisk();
        frame.add(new MainPanel(null, config));

        // 窗体可见
        frame.setVisible(true);
    }

}
