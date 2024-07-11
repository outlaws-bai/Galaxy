import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:06
 * @description:
 */

public class TempTest {
    @Test
    public void test() {
    }

    @Test
    public void test2(){
    }

    public static void main(String[] args) {
        // 创建 JFrame
        JFrame frame = new JFrame("JPanel Background Color Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // 创建 JPanel
        JPanel panel = new JPanel();

        // 设置背景颜色
        panel.setBackground(Color.CYAN); // 你可以使用其他颜色，例如 Color.RED, Color.GREEN 等

        // 将 JPanel 添加到 JFrame
        frame.add(panel);

        // 显示 JFrame
        frame.setVisible(true);
    }
}
