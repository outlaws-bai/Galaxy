package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.Theme;
import org.fife.ui.autocomplete.*;
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
import java.util.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class CodeFileHookerPanel extends IHookerPanel<IHttpHooker> {

    private final String CODE_FILE_SUFFIX;

    private JComboBox<String> codeCombo;

    RSyntaxTextArea codeTextArea = new RSyntaxTextArea();

    public CodeFileHookerPanel(Option option, MontoyaApi api, HttpHookService service) {
        super(option, api, service);
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
            if (FileTools.isExist(filepath)) {
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
            FileTools.deleteFiles(filepath);
            reloadExamples();
        });

        resetInput();

    }


    private void reloadExamples() {
        codeCombo.removeAllItems();
        List<String> examples = FileTools.listDir(Constants.HTTP_HOOK_EXAMPLES_DIR);
        examples.stream().filter(x -> x.endsWith(CODE_FILE_SUFFIX)).forEach(x -> codeCombo.addItem(new File(x).getName().replace(CODE_FILE_SUFFIX, "")));
        resetInput();
    }

    private String getFilePath(String item) {
        return Constants.HTTP_HOOK_EXAMPLES_DIR + File.separator + item + CODE_FILE_SUFFIX;
    }

    private void installAutoComplete() {
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setTriggerKey(KeyStroke.getKeyStroke("TAB"));

        ac.setShowDescWindow(true);
        ac.setParameterAssistanceEnabled(true);
        ac.setChoicesWindowSize(640, 360);
        ac.setDescriptionWindowSize(480, 270);

        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoCompleteSingleChoices(true);
        ac.setAutoActivationDelay(800);
        ac.install(codeTextArea);
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
        installAutoComplete();
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


    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        // 关键操作提示
        // CodeUtil - base64
        provider.addCompletion(new ShorthandCompletion(provider, "base64decode",
            "CodeUtil.b64decode(byte[] data)", "Base64 decode -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "base64encode",
            "CodeUtil.b64encode(String data)", "Base64 encode -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "base64encodeToString",
            "CodeUtil.b64encodeToString(byte[] data)", "Base64 encode to string -> String"));
        // CodeUtil - hex
        provider.addCompletion(new ShorthandCompletion(provider, "hexDecode",
            "CodeUtil.hexDecode(byte[] data)", "Hex decode -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "hexEncode",
            "CodeUtil.hexEncode(String data)", "Hex encode -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "hexEncodeToString",
            "CodeUtil.hexEncodeToString(byte[] data)", "Hex encode to string -> String"));
        // HashUtil
        provider.addCompletion(new ShorthandCompletion(provider, "hash",
            "HashUtil.calc(byte[] data, String algorithm)", "Hash calc -> byte[]"));
        // MacUtil
        provider.addCompletion(new ShorthandCompletion(provider, "mac",
            "MacUtil.calc(byte[] data, byte[] secret, String algorithm)", "Mac calc -> byte[]"));
        // JsonUtil
        provider.addCompletion(new ShorthandCompletion(provider, "jsonStrToMap",
            "JsonUtil.jsonStrToMap(String jsonStr)", "json string to Map -> Map"));
        provider.addCompletion(new ShorthandCompletion(provider, "jsonStrToList",
            "JsonUtil.jsonStrToList(String jsonStr)", "json string to List -> List"));
        provider.addCompletion(new ShorthandCompletion(provider, "toJsonStr",
            "JsonUtil.toJsonStr(Object obj)", "Object to json string -> String"));
        // FactorUtil
        provider.addCompletion(new ShorthandCompletion(provider, "randomString",
            "FactorUtil.randomString(int length)", "Random string -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "uuid",
            "FactorUtil.uuid()", "generate UUID -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "currentDate",
            "FactorUtil.currentDate()", "Get Current Date(yyyy-MM-dd HH:mm:ss) -> String"));
        // Crypto - AES
        provider.addCompletion(new ShorthandCompletion(provider, "aesEncrypt",
            "CryptoUtil.aesEncrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "AES encrypt -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "aesDecrypt",
            "CryptoUtil.aesDecrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "AES decrypt -> byte[]"));
        // Crypto - RSA
        provider.addCompletion(new ShorthandCompletion(provider, "rsaEncrypt",
            "CryptoUtil.rsaEncrypt(String transformation, byte[] data, byte[] publicKey)", "RSA encrypt -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "rsaDecrypt",
            "CryptoUtil.rsaDecrypt(String transformation, byte[] data, byte[] privateKey)", "RSA decrypt -> byte[]"));
        // Crypto - SM2
        provider.addCompletion(new ShorthandCompletion(provider, "sm2Encrypt",
            "CryptoUtil.sm2Encrypt(byte[] data, byte[] publicKey)", "SM2 encrypt -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "sm2Decrypt",
            "CryptoUtil.sm2Decrypt(byte[] data, byte[] privateKey)", "SM2 decrypt -> byte[]"));
        // Crypto - SM4
        provider.addCompletion(new ShorthandCompletion(provider, "sm4Encrypt",
            "CryptoUtil.sm4Encrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "SM4 encrypt -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "sm4Decrypt",
            "CryptoUtil.sm4Decrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "SM4 decrypt -> byte[]"));
        // Request - method
        provider.addCompletion(new ShorthandCompletion(provider, "request.getMethod",
            "request.getMethod()", "Request get method -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setMethod",
            "request.setMethod(String method)", "Request set method -> void"));
        // Request - Path
        provider.addCompletion(new ShorthandCompletion(provider, "request.getPath",
            "request.getPath()", "Request get path(No Query String) -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setPath",
            "request.setPath(String path)", "Request set path(No Query String) -> void"));
        // Request - Query
        provider.addCompletion(new ShorthandCompletion(provider, "request.getQuery",
            "request.getQuery()", "Request get Query -> Query"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setQuery",
            "request.setQuery(Query query)", "Request set query -> void"));
        // Request - Headers
        provider.addCompletion(new ShorthandCompletion(provider, "request.getHeaders",
            "request.getHeaders()", "Request get headers -> Headers"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setHeaders",
            "request.setHeaders(Headers headers)", "Request set headers -> void"));
        // Request - Body
        provider.addCompletion(new ShorthandCompletion(provider, "request.getContent",
            "request.getContent()", "Request get content(byte[] body) -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setContent",
            "request.setContent(byte[] content)", "Request set content(byte[] body) -> void"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.getBody",
            "request.getBody()", "Request get body -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setBody",
            "request.setBody(String body)", "Request set body -> void"));
        // Response - Body
        provider.addCompletion(new ShorthandCompletion(provider, "response.getContent",
            "response.getContent()", "Response get content(byte[] body) -> byte[]"));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setContent",
            "response.setContent(byte[] content)", "Response set content(byte[] body) -> void"));
        provider.addCompletion(new ShorthandCompletion(provider, "response.getBody",
            "response.getBody()", "Response get body -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setBody",
            "response.setBody(String body)", "Response set body -> void"));
        // Response - Status Code
        provider.addCompletion(new ShorthandCompletion(provider, "response.getStatusCode",
            "response.getStatusCode()", "Response get status code -> String"));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setStatusCode",
            "response.setStatusCode(String body)", "Response set status code -> void"));
        // Response - Headers
        provider.addCompletion(new ShorthandCompletion(provider, "response.getHeaders",
            "response.getHeaders()", "Response get headers -> Headers"));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setHeaders",
            "response.setHeaders(Headers headers)", "Response set headers -> void"));
        // Headers
        provider.addCompletion(new ShorthandCompletion(provider, "headers.add",
            "headers.add(String key, String value)", "add(append) -> void"));
        provider.addCompletion(new ShorthandCompletion(provider, "headers.put",
            "headers.put(String key, String value)", "put(cover) -> void"));
        provider.addCompletion(new ShorthandCompletion(provider, "headers.remove",
            "headers.remove(String key)", "remove -> void"));
        // Query
        provider.addCompletion(new ShorthandCompletion(provider, "query.add",
            "query.add(String key, String value)", "add(append) -> void"));
        provider.addCompletion(new ShorthandCompletion(provider, "query.put",
            "query.put(String key, String value)", "put(cover) -> void"));
        provider.addCompletion(new ShorthandCompletion(provider, "query.remove",
            "query.remove(String key)", "remove -> void"));
        // log
        provider.addCompletion(new ShorthandCompletion(provider, "log.info",
            "log.info(\"request: {}\", request)", "log info -> void"));

        return provider;
    }
}
