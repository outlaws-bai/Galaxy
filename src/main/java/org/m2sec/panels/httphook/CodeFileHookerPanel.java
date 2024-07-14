package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.Theme;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.m2sec.Galaxy;
import org.m2sec.core.common.*;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.httphook.IHttpHooker;
import org.m2sec.core.httphook.JavaFileHookerFactor;
import org.m2sec.core.httphook.JsHookerFactor;
import org.m2sec.core.httphook.PythonHookerFactor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class CodeFileHookerPanel extends IHookerPanel<IHttpHooker> {

    private final String CODE_LANGUAGE;

    private final String CODE_FILE_SUFFIX;

    private JComboBox<String> codeCombo;

    RSyntaxTextArea codeTextArea = new RSyntaxTextArea();

    public CodeFileHookerPanel(Option option, MontoyaApi api, HttpHookService service) {
        super(option, api, service);
        CODE_LANGUAGE = Helper.capitalizeFirstLetter(service.name());
        if (service.equals(HttpHookService.JAVA)) {
            CODE_FILE_SUFFIX = Constants.JAVA_FILE_SUFFIX;
        } else if (service.equals(HttpHookService.PYTHON)) {
            CODE_FILE_SUFFIX = Constants.PYTHON_FILE_SUFFIX;
        } else if (service.equals(HttpHookService.JS)) {
            CODE_FILE_SUFFIX = Constants.JS_FILE_SUFFIX;
        } else {
            throw new InputMismatchException(service.name());
        }
        initPanel();
    }

    private void initPanel() {
        codeCombo = new JComboBox<>();
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
//        setBackground(Color.red);
        setLayout(new BorderLayout());
        // 创建顶部下拉框的面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel selectLabel = new JLabel("Code file: ");
        selectLabel.setToolTipText(Constants.HOOK_BY_CODE_IMPL_DEF);
        JPanel rightPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton newButton = new JButton("New");
        JButton deleteButton = new JButton("Delete");
        rightPanel.add(selectLabel);
        rightPanel.add(codeCombo);
        rightPanel.add(saveButton);
        rightPanel.add(newButton);
        rightPanel.add(deleteButton);
        reloadExamples();

        topPanel.add(rightPanel, BorderLayout.WEST);


        // 创建 RTextScrollPane 并添加到 JFrame
        RTextScrollPane scrollPane = new RTextScrollPane(codeTextArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
//        setBackground(Color.blue);

        codeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                codeTextArea.setVisible(true);
                codeTextArea.setText(FileTools.readFileAsString(getFilePath((String) e.getItem())));
                codeTextArea.setCaretPosition(0);
            } else {
                codeTextArea.setVisible(false);
            }
        });

        saveButton.addActionListener(e -> FileTools.writeFile(getFilePath((String) codeCombo.getSelectedItem()),
            codeTextArea.getText()));

        newButton.addActionListener(e -> {
            String filename = SwingTools.showInputDialog("Please input filename: ");
            if (filename == null) return;
            String filepath = getFilePath(filename.replace(CODE_FILE_SUFFIX, ""));
            if(FileTools.fileIsExist(filepath)) {
                SwingTools.showErrorDialog("This already exists, please try again. ");
                return;
            }
            FileTools.createFiles(filepath);
            String content;
            if (service.equals(HttpHookService.JAVA))
                content = Render.renderTemplate(FileTools.readResourceAsString("templates/HttpHookTemplate" +
                    CODE_FILE_SUFFIX), new HashMap<>(Map.of("filename", filename)));
            else content = FileTools.readResourceAsString("templates/HttpHookTemplate" +
                CODE_FILE_SUFFIX);
            FileTools.writeFile(filepath, content);
            reloadExamples();
            codeCombo.setSelectedItem(filename);
        });

        deleteButton.addActionListener(e -> {
            if (codeCombo.getSelectedIndex() == -1) return;
            String selectItem = (String) codeCombo.getSelectedItem();
            String filepath = getFilePath(selectItem);
            boolean res = SwingTools.showConfirmDialog(String.format("Are you sure you want to delete this: %s?",
                selectItem));
            if (!res) return;
            FileTools.deleteFileIfExist(filepath);
            reloadExamples();
        });

        resetInput();

    }


    private void reloadExamples() {
        codeCombo.removeAllItems();
        List<String> examples = FileTools.listDir(Constants.HTTP_HOOK_EXAMPLES_DIR);
        examples.stream().filter(x -> x.endsWith(CODE_FILE_SUFFIX)).forEach(x -> {
            codeCombo.addItem(new File(x).getName().replace(CODE_FILE_SUFFIX, ""));
        });
        resetInput();
    }

    private String getFilePath(String item) {
        return Constants.HTTP_HOOK_EXAMPLES_DIR + File.separator + item + CODE_FILE_SUFFIX;
    }

    public void resetCodeTheme() {
        if (service.equals(HttpHookService.JAVA)) {
            codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        } else if (service.equals(HttpHookService.PYTHON)) {
            codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        } else if (service.equals(HttpHookService.JS)) {
            codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        } else {
            throw new InputMismatchException(service.name());
        }
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
    }


    @Override
    public IHttpHooker newHooker() {
        FileTools.writeFile(FileTools.getExampleScriptFilePath(option.getCodeSelectItem(),
            CODE_FILE_SUFFIX), codeTextArea.getText());
        if (service.equals(HttpHookService.JAVA)) {
            return new JavaFileHookerFactor();
        } else if (service.equals(HttpHookService.PYTHON)) {
            return new PythonHookerFactor();
        } else if (service.equals(HttpHookService.JS)) {
            return new JsHookerFactor();
        } else {
            throw new InputMismatchException(service.name());
        }
    }


    @Override
    public String getInput() {
        return (String) codeCombo.getSelectedItem();
    }

    @Override
    public void resetInput() {
        String codeSelectItem = option.getCodeSelectItem();
        if (codeSelectItem != null && !codeSelectItem.isBlank()) {
            codeTextArea.setVisible(true);
            codeCombo.setSelectedItem(option.getCodeSelectItem());
            codeTextArea.setText(FileTools.readFileAsString(getFilePath((String) codeCombo.getSelectedItem())));
        } else {
            if (codeCombo.getItemCount() > 0) {
                codeTextArea.setVisible(true);
                codeCombo.setSelectedIndex(0);
                codeTextArea.setText(FileTools.readFileAsString(getFilePath((String) codeCombo.getSelectedItem())));
            } else {
                codeCombo.setSelectedIndex(-1);
                codeTextArea.setVisible(false);
            }
        }
        codeTextArea.setCaretPosition(0);
    }
}
