package org.m2sec.panels.httphook;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Render;
import org.m2sec.core.utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class JavaJPanel extends JPanel {
    private final HashMap<String, Object> cache;

    private static final String javaFileSuffix = ".java";

    public JavaJPanel(HashMap<String, Object> cache) {
        this.cache = cache;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        // 创建顶部下拉框的面板
        JPanel topPanel = new JPanel();
        JComboBox<String> codeCombo = new JComboBox<>();
        JButton saveButton = new JButton("Save");
        JButton newButton = new JButton("New");
        JButton deleteButton = new JButton("Delete");
        reloadExamples(codeCombo);
        codeCombo.setSelectedIndex(0);

        topPanel.add(codeCombo);
        topPanel.add(saveButton);
        topPanel.add(newButton);
        topPanel.add(deleteButton);

        // 创建 RSyntaxTextArea 实例
        RSyntaxTextArea codeTextArea = new RSyntaxTextArea(20, 60);
        codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeTextArea.setCodeFoldingEnabled(true);
        codeTextArea.setText(FileUtil.readFileAsString(getFilePath((String) codeCombo.getSelectedItem())));

        // 创建 RTextScrollPane 并添加到 JFrame
        RTextScrollPane scrollPane = new RTextScrollPane(codeTextArea);

        add(new JLabel("This is JAVA"), BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
//        setBackground(Color.blue);

        codeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                codeTextArea.setText(FileUtil.readFileAsString(getFilePath((String) e.getItem())));
            }
        });

        saveButton.addActionListener(e -> FileUtil.overwriteFile(getFilePath((String) codeCombo.getSelectedItem()),
            codeTextArea.getText()));

        newButton.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog(null, "Please input filename: ");
            if (filename != null) {
                String filepath = getFilePath(filename.replace(javaFileSuffix, ""));
                FileUtil.createFiles(filepath);
                String content = Render.renderTemplate(FileUtil.readResourceAsString("HookTemplate.java"),
                    new HashMap<>(Map.of("filename", filename)));
                FileUtil.writeFile(filepath, content);
                reloadExamples(codeCombo);
                codeCombo.setSelectedItem(filename);
            }
        });

        deleteButton.addActionListener(e -> {
            String filepath = getFilePath((String) codeCombo.getSelectedItem());
            FileUtil.deleteFileIfExist(filepath);
            reloadExamples(codeCombo);
            codeCombo.setSelectedIndex(0);
        });
    }

    private void reloadExamples(JComboBox<String> codeCombo) {
        codeCombo.removeAllItems();
        List<String> examples = FileUtil.listDir(Constants.HTTP_HOOK_EXAMPLES_FILE_DIR);
        examples.stream().filter(x -> new File(x).getName().endsWith(javaFileSuffix)).forEach(x -> codeCombo.addItem(new File(x).getName().replace(javaFileSuffix, "")));

    }

    private String getFilePath(String item) {
        return Constants.HTTP_HOOK_EXAMPLES_FILE_DIR + File.separator + item + javaFileSuffix;
    }

}
