package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.Theme;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.m2sec.Galaxy;
import org.m2sec.core.common.CacheInfo;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Render;
import org.m2sec.core.utils.FileUtil;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class JavaJPanel extends JPanel {
    private final CacheInfo cache;
    private final MontoyaApi api;

    private static final String javaFileSuffix = ".java";

    private final JComboBox<String> codeCombo = new JComboBox<>();

    public JavaJPanel(CacheInfo cache, MontoyaApi api) {
        this.cache = cache;
        this.api = api;
        initPanel();
    }

    private void initPanel() {
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
//        setBackground(Color.red);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        setLayout(new BorderLayout());
        // 创建顶部下拉框的面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        JLabel descLabel = new JLabel(Constants.HTTP_HOOK_JAVA_DEF);
        JLabel selectLabel = new JLabel("Select JAVA File: ");
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton newButton = new JButton("New");
        JButton deleteButton = new JButton("Delete");
        rightPanel.add(selectLabel);
        rightPanel.add(codeCombo);
        rightPanel.add(saveButton);
        rightPanel.add(newButton);
        rightPanel.add(deleteButton);
        reloadExamples(codeCombo);

        topPanel.add(descLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // 创建 RSyntaxTextArea 实例
        RSyntaxTextArea codeTextArea = new RSyntaxTextArea();
        codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeTextArea.setAntiAliasingEnabled(true);
        codeTextArea.setAutoIndentEnabled(true);
        codeTextArea.setPaintTabLines(true);
        codeTextArea.setTabSize(4);
        codeTextArea.setCodeFoldingEnabled(true);
        codeTextArea.setTabsEmulated(true);
        codeTextArea.setHighlightCurrentLine(true);
        if (Galaxy.isInBurp() && api.userInterface().currentTheme().equals(Theme.DARK)) {
            try {
                org.fife.ui.rsyntaxtextarea.Theme.load(getClass().getResourceAsStream("/org/fife/ui" +
                    "/rsyntaxtextarea/themes/dark.xml")).apply(codeTextArea);
                codeTextArea.setFont(api.userInterface().currentEditorFont());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 创建 RTextScrollPane 并添加到 JFrame
        RTextScrollPane scrollPane = new RTextScrollPane(codeTextArea);

        add(new JLabel("This is JAVA"), BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
//        setBackground(Color.blue);

        codeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                codeTextArea.setText(FileUtil.readFileAsString(getFilePath((String) e.getItem())));
                codeTextArea.setCaretPosition(0);
            }
        });

        saveButton.addActionListener(e -> FileUtil.writeFile(getFilePath((String) codeCombo.getSelectedItem()),
            codeTextArea.getText()));

        newButton.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog(null, "Please input filename: ");
            if (filename != null) {
                String filepath = getFilePath(filename.replace(javaFileSuffix, ""));
                FileUtil.createFiles(filepath);
                String content = Render.renderTemplate(FileUtil.readResourceAsString("templates/HttpHookTemplate.java"),
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

        setData();
        codeTextArea.setText(FileUtil.readFileAsString(getFilePath((String) codeCombo.getSelectedItem())));

    }

    private void setData() {
        codeCombo.setSelectedItem(cache.getJavaSelectItem());
    }

    public String getData() {
        return (String) codeCombo.getSelectedItem();
    }

    private void reloadExamples(JComboBox<String> codeCombo) {
        codeCombo.removeAllItems();
        List<String> examples = FileUtil.listDir(Constants.HTTP_HOOK_EXAMPLES_FILE_DIR);
        examples.stream().filter(x -> new File(x).getName().endsWith(javaFileSuffix)).forEach(x -> codeCombo.addItem(new File(x).getName().replace(javaFileSuffix, "")));
        setData();
    }

    private String getFilePath(String item) {
        return Constants.HTTP_HOOK_EXAMPLES_FILE_DIR + File.separator + item + javaFileSuffix;
    }

}
