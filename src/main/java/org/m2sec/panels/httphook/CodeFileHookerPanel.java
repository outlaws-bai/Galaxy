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
import org.m2sec.core.httphook.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
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

    public static final JLabel selectLabel0 = new JLabel("Hook Script:");

    RSyntaxTextArea codeTextArea = new RSyntaxTextArea();

    public CodeFileHookerPanel(Config config, MontoyaApi api, HttpHookService service) {
        super(config, api, service);
        if (service.equals(HttpHookService.JAVA)) {
            CODE_FILE_SUFFIX = Constants.JAVA_FILE_SUFFIX;
        } else if (service.equals(HttpHookService.GRAALPY)) {
            CODE_FILE_SUFFIX = Constants.GRAALPY_FILE_SUFFIX;
        } else if (service.equals(HttpHookService.JS)) {
            CODE_FILE_SUFFIX = Constants.JS_FILE_SUFFIX;
        } else if (service.equals(HttpHookService.JYTHON)) {
            CODE_FILE_SUFFIX = Constants.JYTHON_FILE_SUFFIX;
        } else {
            throw new InputMismatchException(service.name());
        }
        initPanel();
    }

    private void initPanel() {
        codeCombo = new JComboBox<>();
        setLayout(new BorderLayout());
        // 创建顶部下拉框的面板
        JLabel selectLabel = new JLabel("Hook Script:");
        JPanel topPanel = new JPanel(new BorderLayout());
        selectLabel.setPreferredSize(new Dimension(CodeFileHookerPanel.getDescWidth(),
            selectLabel.getPreferredSize().height));
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
                SwingTools.showErrorMessageDialog("This already exists, please try again. ");
                return;
            }
            FileTools.createFiles(filepath);
            String content;
            if (service.equals(HttpHookService.JAVA))
                content =
                    Render.renderTemplate(FileTools.readResourceAsString("templates/HttpHookTemplate" + CODE_FILE_SUFFIX), new HashMap<>(Map.of("filename", filename)));
            else content = FileTools.readResourceAsString("templates/HttpHookTemplate" + CODE_FILE_SUFFIX);
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
        if (CompatTools.isMac()) {
            ac.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, KeyEvent.META_DOWN_MASK));
        } else {
            ac.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, KeyEvent.CTRL_DOWN_MASK));
        }
        ac.setShowDescWindow(true);
        ac.setParameterAssistanceEnabled(true);
        ac.setChoicesWindowSize(640, 360);
        ac.setDescriptionWindowSize(480, 270);
        ac.setAutoCompleteSingleChoices(false);
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(800);
        ac.install(codeTextArea);
    }

    public static int getDescWidth() {
        int width = selectLabel0.getPreferredSize().width;
        return ((width + 9) / 10) * 10;
    }

    public void resetCodeTheme() {
        if (service.equals(HttpHookService.JAVA)) {
            codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        } else if (service.equals(HttpHookService.GRAALPY) || service.equals(HttpHookService.JYTHON)) {
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
        if (Galaxy.isInBurp()) {
            if (api.userInterface().currentTheme().equals(Theme.DARK)) {
                try {
                    org.fife.ui.rsyntaxtextarea.Theme.load(getClass().getResourceAsStream("/org/fife/ui" +
                        "/rsyntaxtextarea/themes/dark.xml")).apply(codeTextArea);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            codeTextArea.setFont(api.userInterface().currentEditorFont());
        }
        installAutoComplete();
    }


    @Override
    public IHttpHooker newHooker() {
        FileTools.writeFile(FileTools.getExampleScriptFilePath(config.getOption().getCodeSelectItem(),
                CODE_FILE_SUFFIX),
            codeTextArea.getText());
        if (service.equals(HttpHookService.JAVA)) {
            return new JavaFileHookerFactor();
        } else if (service.equals(HttpHookService.GRAALPY)) {
            return new GraalpyHookerFactor();
        } else if (service.equals(HttpHookService.JS)) {
            return new JsHookerFactor();
        } else if (service.equals(HttpHookService.JYTHON)) {
            return new JythonHookerFactor();
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
        Option option = config.getOption();
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
        provider.addCompletion(new ShorthandCompletion(provider, "base64decode", "CodeUtil.b64decode(byte[] data)",
            "Base64 decode -> byte[]", SwingTools.renderLink("CodeUtil.b64decode(byte[] data)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "base64encode", "CodeUtil.b64encode(String data)",
            "Base64 encode -> byte[]", SwingTools.renderLink("CodeUtil.b64encode(String data)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "base64encodeToString", "CodeUtil.b64encodeToString" +
            "(byte[] data)", "Base64 encode to string -> String", SwingTools.renderLink("Base64 encode to string -> " +
            "String", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil" +
            ".java")));
        // CodeUtil - hex
        provider.addCompletion(new ShorthandCompletion(provider, "hexDecode", "CodeUtil.hexDecode(byte[] data)", "Hex" +
            " decode -> byte[]", SwingTools.renderLink("CodeUtil.hexDecode(byte[] data)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "hexEncode", "CodeUtil.hexEncode(String data)", "Hex" +
            " encode -> byte[]", SwingTools.renderLink("CodeUtil.hexEncode(String data)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "hexEncodeToString", "CodeUtil.hexEncodeToString" +
            "(byte[] data)", "Hex encode to string -> String", SwingTools.renderLink("CodeUtil.hexEncodeToString" +
            "(byte[] data)", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils" +
            "/CodeUtil.java")));
        // JsonUtil
        provider.addCompletion(new ShorthandCompletion(provider, "jsonStrToMap", "JsonUtil.jsonStrToMap(String " +
            "jsonStr)", "json string to Map -> Map", SwingTools.renderLink("JsonUtil.jsonStrToMap(String jsonStr)",
            "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "jsonStrToList", "JsonUtil.jsonStrToList(String " +
            "jsonStr)", "json string to List -> List", SwingTools.renderLink("JsonUtil.jsonStrToList(String jsonStr)"
            , "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "toJsonStr", "JsonUtil.toJsonStr(Object obj)",
            "Object to json string -> String", SwingTools.renderLink("JsonUtil.toJsonStr(Object obj)", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java")));
        // FactorUtil
        provider.addCompletion(new ShorthandCompletion(provider, "randomString", "FactorUtil.randomString(int length)"
            , "Random string -> String", SwingTools.renderLink("FactorUtil.randomString(int length)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/FactorUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "uuid", "FactorUtil.uuid()", "generate UUID -> " +
            "String", SwingTools.renderLink("FactorUtil.uuid()", "https://github.com/outlaws-bai/Galaxy/blob/main/src" +
            "/main/java/org/m2sec/core/utils/FactorUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "currentDate", "FactorUtil.currentDate()", "Get " +
            "Current Date(yyyy-MM-dd HH:mm:ss) -> String", SwingTools.renderLink("FactorUtil.currentDate()", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/FactorUtil.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "currentTime", "FactorUtil.currentTime()", "Get " +
            "Current Time Stamp -> Long", SwingTools.renderLink("FactorUtil.currentTime()", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/FactorUtil.java")));


        // Request - method
        provider.addCompletion(new ShorthandCompletion(provider, "request.getMethod", "request.getMethod()", "Request" +
            " get method -> String", SwingTools.renderLink("request.getMethod()", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setMethod", "request.setMethod(String " +
            "method)", "Request set method -> void", SwingTools.renderLink("request.setMethod(String method)", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        // Request - Path
        provider.addCompletion(new ShorthandCompletion(provider, "request.getPath", "request.getPath()", "Request get" +
            " path(No Query String) -> String", SwingTools.renderLink("request.getPath()", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setPath", "request.setPath(String path)",
            "Request set path(No Query String) -> void", SwingTools.renderLink("request.setPath(String path)", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        // Request - Query
        provider.addCompletion(new ShorthandCompletion(provider, "request.getQuery", "request.getQuery()", "Request " +
            "get Query -> Query", SwingTools.renderLink("request.getQuery()", "https://github.com/outlaws-bai/Galaxy" +
            "/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setQuery", "request.setQuery(Query query)",
            "Request set query -> void", SwingTools.renderLink("request.setQuery(Query query)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        // Request - Form
        provider.addCompletion(new ShorthandCompletion(provider, "request.getForm", "request.getForm()", "Request get" +
            " Form -> Form", SwingTools.renderLink("request.getForm()", "https://github.com/outlaws-bai/Galaxy/blob" +
            "/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setForm", "request.setForm(Form form)",
            "Request set Form -> void", SwingTools.renderLink("request.setForm(Form form)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        // Request - Headers
        provider.addCompletion(new ShorthandCompletion(provider, "request.getHeaders", "request.getHeaders()",
            "Request get headers -> Headers", SwingTools.renderLink("request.getHeaders()", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setHeaders", "request.setHeaders(Headers " +
            "headers)", "Request set headers -> void", SwingTools.renderLink("request.setHeaders(Headers headers)",
            "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        // Request - Body
        provider.addCompletion(new ShorthandCompletion(provider, "request.getContent", "request.getContent()",
            "Request get content(byte[] body) -> byte[]", SwingTools.renderLink("request.getContent()", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setContent", "request.setContent(byte[] " +
            "content)", "Request set content(byte[] body) -> void", SwingTools.renderLink("request.setContent(byte[] " +
            "content)", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request" +
            ".java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.getBody", "request.getBody()", "Request get" +
            " body -> String", SwingTools.renderLink("request.getBody()", "https://github.com/outlaws-bai/Galaxy/blob" +
            "/main/src/main/java/org/m2sec/core/models/Request.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "request.setBody", "request.setBody(String body)",
            "Request set body -> void", SwingTools.renderLink("request.setBody(String body)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java")));
        // Response - Body
        provider.addCompletion(new ShorthandCompletion(provider, "response.getContent", "response.getContent()",
            "Response get content(byte[] body) -> byte[]", SwingTools.renderLink("response.getContent()", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setContent", "response.setContent(byte[] " +
            "content)", "Response set content(byte[] body) -> void", SwingTools.renderLink("response.setContent" +
            "(byte[] content)", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models" +
            "/Response.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "response.getBody", "response.getBody()", "Response " +
            "get body -> String", SwingTools.renderLink("response.getBody()", "https://github.com/outlaws-bai/Galaxy" +
            "/blob/main/src/main/java/org/m2sec/core/models/Response.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setBody", "response.setBody(String body)",
            "Response set body -> void", SwingTools.renderLink("response.setBody(String body)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java")));
        // Response - Status Code
        provider.addCompletion(new ShorthandCompletion(provider, "response.getStatusCode", "response.getStatusCode()"
            , "Response get status code -> String", SwingTools.renderLink("response.getStatusCode()", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setStatusCode", "response.setStatusCode" +
            "(String body)", "Response set status code -> void", SwingTools.renderLink("response.setStatusCode(String" +
            " body)", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response" +
            ".java")));
        // Response - Headers
        provider.addCompletion(new ShorthandCompletion(provider, "response.getHeaders", "response.getHeaders()",
            "Response get headers -> Headers", SwingTools.renderLink("response.getHeaders()", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "response.setHeaders", "response.setHeaders(Headers " +
            "headers)", "Response set headers -> void", SwingTools.renderLink("response.setHeaders(Headers headers)",
            "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java")));
        // Headers
        provider.addCompletion(new ShorthandCompletion(provider, "headers.has", "headers.has(String key)", "has -> " +
            "boolean", SwingTools.renderLink("headers.has(String key)", "https://github.com/outlaws-bai/Galaxy/blob" +
            "/main/src/main/java/org/m2sec/core/models/Headers.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "headers.hasIgnoreCase", "headers.hasIgnoreCase" +
            "(String key)", "has Ignore Case -> boolean", SwingTools.renderLink("headers.hasIgnoreCase(String key)",
            "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Headers.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "headers.add", "headers.add(String key, String " +
            "value)", "add(append) -> void", SwingTools.renderLink("headers.add(String key, String value)", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Headers.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "headers.put", "headers.put(String key, String " +
            "value)", "put(cover) -> void", SwingTools.renderLink("headers.put(String key, String value)", "https" +
            "://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Headers.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "headers.remove", "headers.remove(String key)",
            "remove -> void", SwingTools.renderLink("headers.remove(String key)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Headers.java")));
        // Query
        provider.addCompletion(new ShorthandCompletion(provider, "query.add", "query.add(String key, String value)",
            "add(append) -> void", SwingTools.renderLink("query.add(String key, String value)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Query.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "query.put", "query.put(String key, String value)",
            "put(cover) -> void", SwingTools.renderLink("query.put(String key, String value)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Query.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "query.remove", "query.remove(String key)", "remove " +
            "-> void", SwingTools.renderLink("query.remove(String key)", "https://github.com/outlaws-bai/Galaxy/blob" +
            "/main/src/main/java/org/m2sec/core/models/Query.java")));
        // Form
        provider.addCompletion(new ShorthandCompletion(provider, "form.add", "form.add(String key, String value)",
            "add(append) -> void", SwingTools.renderLink("form.add(String key, String value)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Form.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "form.put", "form.put(String key, String value)",
            "put(cover) -> void", SwingTools.renderLink("form.put(String key, String value)", "https://github" +
            ".com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Form.java")));
        provider.addCompletion(new ShorthandCompletion(provider, "form.remove", "form.remove(String key)", "remove ->" +
            " void", SwingTools.renderLink("form.remove(String key)", "https://github.com/outlaws-bai/Galaxy/blob" +
            "/main/src/main/java/org/m2sec/core/models/Form.java")));
        // log
        provider.addCompletion(new ShorthandCompletion(provider, "log.info", "log.info(\"request: {}\", request)",
            "log info -> void"));


        // HashUtil
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "hash",
            "HashUtil.calc(String algorithm, byte[] data)",
            "Hash calc -> byte[]",
            SwingTools.renderSummary("hash calc", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org" +
                "/m2sec/core/utils/HashUtil.java", "byte[]", "algorithm(String) MD2 | MD4 | MD5 | SM3 | SHA-1 | " +
                "SHA-224 | SHA-256 | ...", "data(byte[]) origin data")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "hash",
            "HashUtil.calcToHex(String algorithm, byte[] data)",
            "Hash calcToHex -> String",
            SwingTools.renderSummary("hash calcToHex", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main" +
                "/java/org" +
                "/m2sec/core/utils/HashUtil.java", "byte[]", "algorithm(String) MD2 | MD4 | MD5 | SM3 | SHA-1 | " +
                "SHA-224 | SHA-256 | ...", "data(byte[]) origin data")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "hash",
            "HashUtil.calcToBase64(String algorithm, byte[] data)",
            "Hash calcToBase64 -> String",
            SwingTools.renderSummary("hash calcToBase64", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main" +
                "/java/org" +
                "/m2sec/core/utils/HashUtil.java", "byte[]", "algorithm(String) MD2 | MD4 | MD5 | SM3 | SHA-1 | " +
                "SHA-224 | SHA-256 | ...", "data(byte[]) origin data")
        ));
        // MacUtil
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "mac",
            "MacUtil.calc(String algorithm, byte[] data, byte[] secret)",
            "Mac calc -> byte[]",
            SwingTools.renderSummary("mac calc", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org" +
                "/m2sec/core/utils/MacUtil.java", "byte[]", "algorithm(String) HmacMD5 | HmacSHA1 | HmacSHA224 | " +
                "HmacSHA256|...", "data(byte[]) origin data")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "mac",
            "MacUtil.calcToHex(String algorithm, byte[] data, byte[] secret)",
            "Mac calcToHex -> byte[]",
            SwingTools.renderSummary("mac calcToHex", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                "/org" +
                "/m2sec/core/utils/MacUtil.java", "byte[]", "algorithm(String) HmacMD5 | HmacSHA1 | HmacSHA224 | " +
                "HmacSHA256|...", "data(byte[]) origin data")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "mac",
            "MacUtil.calcToBase64(String algorithm, byte[] data, byte[] secret)",
            "Mac calcToBase64 -> byte[]",
            SwingTools.renderSummary("mac calcToBase64", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main" +
                "/java/org" +
                "/m2sec/core/utils/MacUtil.java", "byte[]", "algorithm(String) HmacMD5 | HmacSHA1 | HmacSHA224 | " +
                "HmacSHA256|...", "data(byte[]) origin data")
        ));
        // Crypto TEA
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "teaEncrypt",
            "CryptoUtil.teaEncrypt(String transformation, byte[] data, byte[] secret)",
            "TEA encrypt -> byte[]",
            SwingTools.renderSummary("DES encrypt",
                "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CryptoUtil.java",
                "byte[]",
                "transformation(String) TEA | XTEA | XXTEA",
                "data(byte[]) origin data",
                "secret(byte[]) secret"
            )
        ));
        provider.addCompletion(new ShorthandCompletion
            (provider,
                "teaDecrypt",
                "CryptoUtil.teaDecrypt(String transformation, byte[] data, byte[] secret)",
                "TEA decrypt -> byte[]",
                SwingTools.renderSummary("DES decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main" +
                    "/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) TEA | " +
                    "XTEA | XXTEA", "data(byte[]) encrypted data", "secret(byte[]) secret"
                )
            ));
        // Crypto - AES
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "aesEncrypt",
            "CryptoUtil.aesEncrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "AES encrypt -> byte[]",
            SwingTools.renderSummary("AES encrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) AES/ECB/PKCS5Padding |" +
                    " " +
                    "AES/CBC/PKCS5Padding | AES/GCM/NoPadding", "data(byte[]) origin data", "secret(byte[]) secret",
                "params(Map<String, Object>) encrypt params")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "aesDecrypt",
            "CryptoUtil.aesDecrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "AES decrypt -> byte[]",
            SwingTools.renderSummary("AES decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) AES/ECB/PKCS5Padding |" +
                    " " +
                    "AES/CBC/PKCS5Padding | AES/GCM/NoPadding", "data(byte[]) encrypted data", "secret(byte[]) secret",
                "params(Map<String, Object>) decrypt params")));
        // Crypto DES
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "desEncrypt",
            "CryptoUtil.desEncrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "DES encrypt -> byte[]",
            SwingTools.renderSummary("DES encrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) DES/ECB/PKCS5Padding |" +
                    " " +
                    "DES/CBC/PKCS5Padding | DES/GCM/NoPadding", "data(byte[]) origin data", "secret(byte[]) secret",
                "params(Map<String, Object>) encrypt params")
        ));
        provider.addCompletion(new ShorthandCompletion(provider,
            "desDecrypt",
            "CryptoUtil.desDecrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "DES decrypt -> byte[]",
            SwingTools.renderSummary("DES decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) DES/ECB/PKCS5Padding |" +
                    " " +
                    "DES/CBC/PKCS5Padding | DES/GCM/NoPadding", "data(byte[]) encrypted data", "secret(byte[]) secret",
                "params(Map<String, Object>) decrypt params")
        ));
        // Crypto 3DES
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "des3Encrypt",
            "CryptoUtil.des3Encrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "DES3 encrypt -> byte[]",
            SwingTools.renderSummary("DES3 encrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) " +
                    "DESede/ECB/PKCS5Padding | " +
                    "DESede/CBC/PKCS5Padding | DESede/GCM/NoPadding", "data(byte[]) origin data", "secret(byte[]) " +
                    "secret"
                , "params(Map<String, Object>) encrypt params")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "des3Decrypt",
            "CryptoUtil.des3Decrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "DES3 decrypt -> byte[]",
            SwingTools.renderSummary("DES3 decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) DESede/ECB/PKCS5Padding | " +
                "DESede/CBC/PKCS5Padding | DESede/GCM/NoPadding", "data(byte[]) encrypted data", "secret(byte[]) " +
                "secret", "params(Map<String, Object>) decrypt params")
        ));
        // Crypto - SM4
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "sm4Encrypt",
            "CryptoUtil.sm4Encrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "SM4 encrypt -> byte[]",
            SwingTools.renderSummary("SM4 encrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) SM4/ECB/PKCS5Padding |" +
                    " " +
                    "SM4/CBC/PKCS5Padding | SM4/GCM/NoPadding", "data(byte[]) origin data", "secret(byte[]) secret",
                "params(Map<String, Object>) encrypt params")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "sm4Decrypt",
            "CryptoUtil.sm4Decrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params)",
            "SM4 decrypt -> byte[]",
            SwingTools.renderSummary("SM4 decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                    "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) SM4/ECB/PKCS5Padding |" +
                    " " +
                    "SM4/CBC/PKCS5Padding | SM4/GCM/NoPadding", "data(byte[]) encrypted data", "secret(byte[]) secret",
                "params(Map<String, Object>) decrypt params")
        ));
        // Crypto - RSA
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "rsaEncrypt",
            "CryptoUtil.rsaEncrypt(String transformation, byte[] data, byte[] publicKey)",
            "RSA encrypt -> byte[]",
            SwingTools.renderSummary("RSA encrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) RSA/ECB/PKCS1Padding | " +
                "RSA/ECB/OAEPWithSHA-1AndMGF1Padding | RSA/ECB/OAEPWithSHA-256AndMGF1Padding | RSA/ECB/NoPadding | " +
                "RSA/ECB/ISO9796-1Padding", "data(byte[]) origin data", "publicKey(byte[]) public key byte array")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "rsaDecrypt",
            "CryptoUtil.rsaDecrypt(String transformation, byte[] data, byte[] privateKey)",
            "RSA decrypt -> byte[]",
            SwingTools.renderSummary("RSA decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "transformation(String) RSA/ECB/PKCS1Padding | " +
                "RSA/ECB/OAEPWithSHA-1AndMGF1Padding | RSA/ECB/OAEPWithSHA-256AndMGF1Padding | RSA/ECB/NoPadding | " +
                "RSA/ECB/ISO9796-1Padding", "data(byte[]) encrypted data", "publicKey(byte[]) public key byte array")
        ));
        // Crypto - SM2
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "sm2Encrypt",
            "CryptoUtil.sm2Encrypt(String mode, byte[] data, byte[] publicKey)",
            "SM2 encrypt -> byte[]",
            SwingTools.renderSummary("SM2 encrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "mode(String) c1c2c3 | c1c3c2", "data(byte[]) " +
                "origin data", "publicKey(byte[]) public key byte array")
        ));
        provider.addCompletion(new ShorthandCompletion(
            provider,
            "sm2Decrypt",
            "CryptoUtil.sm2Decrypt(String mode, byte[] data, byte[] privateKey)",
            "SM2 decrypt -> byte[]",
            SwingTools.renderSummary("SM2 decrypt", "https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java" +
                "/org/m2sec/core/utils/CryptoUtil.java", "byte[]", "mode(String) c1c2c3 | c1c3c2", "data(byte[]) " +
                "encrypted data", "publicKey(byte[]) private key byte array")
        ));

        return provider;
    }
}
