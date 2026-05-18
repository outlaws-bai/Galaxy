package org.m2sec.panels.galaxysql;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import lombok.Getter;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.Constants;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class GalaxySqlPanel extends JPanel {

    private final MontoyaApi api;
    private JSplitPane splitPane;
    private HttpRequestEditor requestViewer;
    private HttpResponseEditor responseViewer;

    private final List<LogEntry> log = new ArrayList<>(); // 记录原始流量
    private final List<LogEntry> log2 = new ArrayList<>(); // 记录攻击流量
    private final List<LogEntry> log3 = new ArrayList<>(); // 用于展现
    private final List<Request_md5> log4_md5 = new ArrayList<>(); // 用于存放数据包的md5

    public int switchs = 1; // 开关 0关 1开
    private int conut = 0; // 记录条数
    private String data_md5_id; // 用于判断目前选中的数据包
    public AbstractTableModel model = new MyModel();
    private int original_data_len; // 记录原始数据包的长度
    private int is_int = 1; // 开关 0关 1开;纯数据是否进行-1,-0
    private String temp_data; // 用于保存临时内容
    private int JTextArea_int = 0; // 自定义payload开关 0关 1开
    private String JTextArea_data_1 = ""; // 文本域的内容
    private int diy_payload_1 = 1; // 自定义payload空格编码开关 0关 1开
    private int diy_payload_2 = 0; // 自定义payload值置空开关 0关 1开
    private int select_row = 0; // 选中表格的行数
    private Table logTable; // 第一个表格框
    private boolean is_cookie = false; // cookie是否要注入，默认关闭
    private String white_URL = "";
    private int white_switchs = 0; // 白名单开关

    private static GalaxySqlPanel instance;

    public static GalaxySqlPanel getInstance(MontoyaApi api) {
        if (instance == null) {
            instance = new GalaxySqlPanel(api);
        }
        return instance;
    }

    public static GalaxySqlPanel getInstance() {
        return instance;
    }

    private GalaxySqlPanel(MontoyaApi api) {
        this.api = api;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane splitPanes = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane splitPanes_2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        logTable = new Table(new LogModel());
        JScrollPane scrollPane = new JScrollPane(logTable);

        JPanel jp = new JPanel(new BorderLayout());
        JLabel jl = new JLabel("==>");

        Table_log2 table = new Table_log2(model);
        JScrollPane pane = new JScrollPane(table);

        jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
        jp.add(scrollPane);
        jp.add(Box.createHorizontalStrut(5));
        jp.add(jl);
        jp.add(Box.createHorizontalStrut(5));
        jp.add(pane);

        // 侧边复选框
        JPanel jps = new JPanel();
        jps.setLayout(new GridLayout(14, 1));
        JLabel jl_1 = new JLabel("特此说明：为解决加密参数，无法进行sql注入扫描问题，添加此功能");
        JLabel jl_2 = new JLabel("功能在XiaSql的基础上进行改动");
        JLabel jl_3 = new JLabel("使用说明：该功能会自动捕获解密后的请求进行扫描，无需其他操作");
        JCheckBox chkbox1 = new JCheckBox("启动插件", true);
        JCheckBox chkbox2 = new JCheckBox("值是数字则进行-1、-0", false);
        JCheckBox chkbox3 = new JCheckBox("自定义payload");
        JCheckBox chkbox4 = new JCheckBox("自定义payload中空格url编码", true);
        JCheckBox chkbox5 = new JCheckBox("自定义payload中参数值置空");
        JCheckBox chkbox6 = new JCheckBox("测试Cookie");
        JLabel jls_5 = new JLabel("如果需要多个域名加白请用,隔开");
        JTextField textField = new JTextField("填写白名单域名");

        JButton btn1 = new JButton("清空列表");
        JButton btn2 = new JButton("加载/重新加载payload");
        JButton btn3 = new JButton("启动白名单");

        JPanel jps_2 = new JPanel(new GridLayout(1, 1));
        JTextArea jta = new JTextArea("%df' and sleep(3)%23\n'and '1'='1", 18, 16);

        try {
            File f = new File(System.getProperty("user.dir"), "xia_SQL_diy_payload.ini");
            if (f.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String str;
                StringBuilder str_data = new StringBuilder();
                while ((str = in.readLine()) != null) {
                    str_data.append(str).append("\n");
                }
                jta.setText(str_data.toString());
                in.close();
            }
        } catch (IOException ignored) {
        }

        jta.setForeground(Color.BLACK);
        jta.setFont(new Font("楷体", Font.BOLD, 16));
        jta.setBackground(Color.LIGHT_GRAY);
        jta.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta);
        jps_2.add(jsp);

        chkbox1.addItemListener(e -> switchs = chkbox1.isSelected() ? 1 : 0);
        chkbox2.addItemListener(e -> is_int = chkbox2.isSelected() ? 1 : 0);
        chkbox3.addItemListener(e -> {
            if (chkbox3.isSelected()) {
                jta.setEditable(true);
                jta.setBackground(Color.WHITE);
                JTextArea_int = 1;
                JTextArea_data_1 = diy_payload_1 == 1 ? jta.getText().replaceAll(" ", "%20") : jta.getText();
            } else {
                jta.setEditable(false);
                jta.setBackground(Color.LIGHT_GRAY);
                JTextArea_int = 0;
            }
        });
        chkbox4.addItemListener(e -> {
            diy_payload_1 = chkbox4.isSelected() ? 1 : 0;
            JTextArea_data_1 = diy_payload_1 == 1 ? jta.getText().replaceAll(" ", "%20") : jta.getText();
        });
        chkbox5.addItemListener(e -> diy_payload_2 = chkbox5.isSelected() ? 1 : 0);
        chkbox6.addItemListener(e -> is_cookie = chkbox6.isSelected());

        btn1.addActionListener(e -> {
            log.clear();
            log2.clear();
            log3.clear();
            log4_md5.clear();
            conut = 0;
            ((LogModel) logTable.getModel()).fireTableDataChanged();
            model.fireTableDataChanged();
        });

        btn2.addActionListener(e -> {
            JTextArea_data_1 = diy_payload_1 == 1 ? jta.getText().replaceAll(" ", "%20") : jta.getText();
            try {
                File f = new File(System.getProperty("user.dir"), "xia_SQL_diy_payload.ini");
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                out.write(JTextArea_data_1);
                out.close();
            } catch (IOException ignored) {
            }
        });

        btn3.addActionListener(e -> {
            if (btn3.getText().equals("启动白名单")) {
                btn3.setText("关闭白名单");
                white_URL = textField.getText();
                white_switchs = 1;
                textField.setEditable(false);
                textField.setForeground(Color.GRAY);
            } else {
                btn3.setText("启动白名单");
                white_switchs = 0;
                textField.setEditable(true);
                textField.setForeground(Color.BLACK);
            }
        });

        jps.add(jl_1);
        jps.add(jl_2);
        jps.add(jl_3);
        jps.add(chkbox1);
        jps.add(chkbox2);
        jps.add(chkbox6);
        jps.add(btn1);
        jps.add(jls_5);
        jps.add(textField);
        jps.add(btn3);
        jps.add(chkbox3);
        jps.add(chkbox4);
        jps.add(chkbox5);
        jps.add(btn2);

        JTabbedPane tabs = new JTabbedPane();
        requestViewer = api.userInterface().createHttpRequestEditor();
        responseViewer = api.userInterface().createHttpResponseEditor();
        tabs.addTab("Request", requestViewer.uiComponent());
        tabs.addTab("Response", responseViewer.uiComponent());

        splitPanes_2.setLeftComponent(jps);
        splitPanes_2.setRightComponent(jps_2);

        splitPanes.setLeftComponent(jp);
        splitPanes.setRightComponent(tabs);

        splitPane.setLeftComponent(splitPanes);
        splitPane.setRightComponent(splitPanes_2);
        splitPane.setDividerLocation(1000);

        add(splitPane, BorderLayout.CENTER);
    }

    public void checkVul(HttpRequest request, HttpResponse response, ToolType toolFlag) {
        if (switchs == 0)
            return;
        // 不捕获插件发出的请求，防止死循环；同时不捕获Intruder发出的请求
        if (toolFlag == ToolType.EXTENSIONS || toolFlag.name().equals("EXTENSIONS")
                || toolFlag.name().equals("EXTENSION") || toolFlag == ToolType.INTRUDER)
            return;

        Thread thread = new Thread(() -> doCheckVul(request, response, toolFlag));
        thread.start();
    }

    private void doCheckVul(HttpRequest request, HttpResponse response, ToolType toolFlag) {
        int is_add = 0;
        String change_sign_1 = "";

        String urlPath = request.url();
        String[] urlSplit = urlPath.split("\\?");
        temp_data = urlSplit[0];

        if (white_switchs == 1) {
            boolean whiteMatched = false;
            for (String w : white_URL.split(",")) {
                if (temp_data.contains(w)) {
                    whiteMatched = true;
                    break;
                }
            }
            if (!whiteMatched)
                return;
        }

        String[] staticFiles = { "jpg", "png", "gif", "css", "js", "pdf", "mp3", "mp4", "avi" };
        for (String sf : staticFiles) {
            if (temp_data.toLowerCase().endsWith("." + sf))
                return;
        }

        for (ParsedHttpParameter para : request.parameters()) {
            if (para.type() == HttpParameterType.URL || para.type() == HttpParameterType.BODY
                    || para.type() == HttpParameterType.JSON
                    || (is_cookie && para.type() == HttpParameterType.COOKIE)) {
                if (is_add == 0)
                    is_add = 1;
                temp_data += "+" + para.name();
            }
        }

        temp_data += "+" + request.method();
        temp_data = MD5(temp_data);

        for (Request_md5 i : log4_md5) {
            if (i.md5_data.equals(temp_data)) {
                if (toolFlag == ToolType.EXTENSIONS || toolFlag.name().equals("EXTENSIONS")
                        || toolFlag.name().equals("EXTENSION")) {
                    temp_data = MD5(String.valueOf(System.currentTimeMillis()));
                } else {
                    return;
                }
            }
        }

        if (is_add != 0) {
            log4_md5.add(new Request_md5(temp_data));
            int row = log.size();
            original_data_len = response == null ? 0 : response.toByteArray().length();
            if (original_data_len <= 0)
                return;

            HttpRequestResponse reqRes = HttpRequestResponse.httpRequestResponse(request, response);
            log.add(new LogEntry(conut++, toolFlag, reqRes, request.url(), "", "", "", temp_data, 0, "run……",
                    response.statusCode()));
            ((LogModel) logTable.getModel()).fireTableRowsInserted(row, row);
        }

        for (ParsedHttpParameter para : request.parameters()) {
            if (!(para.type() == HttpParameterType.URL || para.type() == HttpParameterType.BODY
                    || para.type() == HttpParameterType.JSON
                    || (is_cookie && para.type() == HttpParameterType.COOKIE))) {
                continue;
            }

            String key = para.name();
            String value = para.value();

            ArrayList<String> payloads = new ArrayList<>();
            payloads.add("'");
            payloads.add("''");

            if (is_int == 1 && value.matches("[0-9]+")) {
                payloads.add("-1");
                payloads.add("-0");
            }

            if (JTextArea_int == 1) {
                for (String a : JTextArea_data_1.split("\n")) {
                    if (!a.trim().isEmpty())
                        payloads.add(a);
                }
            }

            int change = 0;
            for (String payload : payloads) {
                int time_1 = 0, time_2 = 0;
                String testValue = value;
                if (JTextArea_int == 1 && diy_payload_2 == 1 && !payload.equals("'") && !payload.equals("''")
                        && !payload.equals("-1") && !payload.equals("-0")) {
                    testValue = "";
                }

                HttpRequest newReq;
                if (para.type() == HttpParameterType.JSON) {
                    // Montoya API can automatically handle JSON parameter updates beautifully
                    newReq = request.withUpdatedParameters(
                            HttpParameter.parameter(key, testValue + payload, HttpParameterType.JSON));
                } else {
                    newReq = request
                            .withUpdatedParameters(HttpParameter.parameter(key, testValue + payload, para.type()));
                }

                // Explicitly add the hook header so tryHookRequestToServer will encrypt it
                if (!newReq.hasHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                    newReq = newReq.withAddedHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "HookedRequest");
                }

                // 1. Encrypt before sending
                HttpRequest encryptedReq = HttpHookHandler.hooker.tryHookRequestToServer(newReq, 0, false);

                time_1 = (int) System.currentTimeMillis();
                HttpRequestResponse rawResReq = api.http().sendRequest(encryptedReq);
                time_2 = (int) System.currentTimeMillis();

                // 2. Decrypt response
                HttpResponse encryptedRes = rawResReq.response();
                HttpResponse testResponse = null;
                if (encryptedRes != null) {
                    testResponse = HttpHookHandler.hooker.tryHookResponseToBurp(encryptedRes, 0, false);
                }

                // Combine decrypted request and decrypted response for UI display
                HttpRequestResponse resReq = HttpRequestResponse.httpRequestResponse(newReq, testResponse);

                int currentLen = testResponse == null ? 0 : testResponse.toByteArray().length();

                String change_sign = "";
                if (payload.equals("'") || payload.equals("-1") || change == 0) {
                    change = currentLen;
                } else {
                    if (payload.equals("''") || payload.equals("-0")) {
                        if (change != currentLen) {
                            if (currentLen == original_data_len) {
                                change_sign = "✔ ==> ?";
                                change_sign_1 = " ✔";
                            } else {
                                change_sign = "✔ " + (change - currentLen);
                                change_sign_1 = " ✔";
                            }
                        }
                    } else {
                        if (time_2 - time_1 >= 3000) {
                            change_sign = "time > 3";
                            change_sign_1 = " ✔";
                        } else {
                            change_sign = "diy payload";
                        }
                    }
                }

                int sc = testResponse == null ? 0 : testResponse.statusCode();
                log2.add(new LogEntry(conut, toolFlag, resReq, newReq.url(), key, testValue + payload, change_sign,
                        temp_data, time_2 - time_1, "end", sc));
            }
        }

        for (LogEntry value : log) {
            if (temp_data.equals(value.data_md5)) {
                value.state = "end!" + change_sign_1;
            }
        }

        ((LogModel) logTable.getModel()).fireTableDataChanged();
        if (select_row < logTable.getRowCount() && select_row >= 0) {
            logTable.setRowSelectionInterval(select_row, select_row);
        }
    }

    private class LogModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            return log.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "#";
                case 1:
                    return "来源";
                case 2:
                    return "URL";
                case 3:
                    return "返回包长度";
                case 4:
                    return "状态";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LogEntry logEntry = log.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return logEntry.id;
                case 1:
                    return logEntry.tool.name();
                case 2:
                    return logEntry.url;
                case 3:
                    return logEntry.requestResponse.response() != null
                            ? logEntry.requestResponse.response().toByteArray().length()
                            : 0;
                case 4:
                    return logEntry.state;
                default:
                    return "";
            }
        }
    }

    private class MyModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            return log3.size();
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "参数";
                case 1:
                    return "payload";
                case 2:
                    return "返回包长度";
                case 3:
                    return "变化";
                case 4:
                    return "用时";
                case 5:
                    return "响应码";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LogEntry logEntry2 = log3.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return logEntry2.parameter;
                case 1:
                    return logEntry2.value;
                case 2:
                    return logEntry2.requestResponse.response() != null
                            ? logEntry2.requestResponse.response().toByteArray().length()
                            : 0;
                case 3:
                    return logEntry2.change;
                case 4:
                    return logEntry2.times;
                case 5:
                    return logEntry2.response_code;
                default:
                    return "";
            }
        }
    }

    private class Table extends JTable {
        public Table(TableModel tableModel) {
            super(tableModel);
        }

        @Override
        public void changeSelection(int row, int col, boolean toggle, boolean extend) {
            LogEntry logEntry = log.get(row);
            data_md5_id = logEntry.data_md5;
            select_row = logEntry.id;

            log3.clear();
            for (LogEntry entry : log2) {
                if (entry.data_md5.equals(data_md5_id))
                    log3.add(entry);
            }
            model.fireTableDataChanged();

            requestViewer.setRequest(logEntry.requestResponse.request());
            if (logEntry.requestResponse.response() != null) {
                responseViewer.setResponse(logEntry.requestResponse.response());
            }
            super.changeSelection(row, col, toggle, extend);
        }
    }

    private class Table_log2 extends JTable {
        public Table_log2(TableModel tableModel) {
            super(tableModel);
        }

        @Override
        public void changeSelection(int row, int col, boolean toggle, boolean extend) {
            LogEntry logEntry = log3.get(row);
            requestViewer.setRequest(logEntry.requestResponse.request());
            if (logEntry.requestResponse.response() != null) {
                responseViewer.setResponse(logEntry.requestResponse.response());
            }
            super.changeSelection(row, col, toggle, extend);
        }
    }

    private static class Request_md5 {
        final String md5_data;

        Request_md5(String md5_data) {
            this.md5_data = md5_data;
        }
    }

    private static class LogEntry {
        final int id;
        final ToolType tool;
        final HttpRequestResponse requestResponse;
        final String url;
        final String parameter;
        final String value;
        final String change;
        final String data_md5;
        final int times;
        final int response_code;
        String state;

        LogEntry(int id, ToolType tool, HttpRequestResponse requestResponse, String url, String parameter, String value,
                String change, String data_md5, int times, String state, int response_code) {
            this.id = id;
            this.tool = tool;
            this.requestResponse = requestResponse;
            this.url = url;
            this.parameter = parameter;
            this.value = value;
            this.change = change;
            this.data_md5 = data_md5;
            this.times = times;
            this.state = state;
            this.response_code = response_code;
        }
    }

    public static String MD5(String key) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            byte[] md = mdInst.digest(key.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : md) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
