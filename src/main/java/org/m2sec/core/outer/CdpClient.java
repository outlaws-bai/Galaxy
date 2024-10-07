package org.m2sec.core.outer;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.m2sec.core.utils.FactorUtil;
import org.m2sec.core.utils.HttpUtil;
import org.m2sec.core.utils.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author: outlaws-bai
 * @date: 2024/10/7 14:52
 * @description:
 */
@Slf4j
public class CdpClient {
    private final WebSocketClient webSocketClient;
    // 用于存储请求UUID和对应的CompletableFuture，以便并发请求处理
    private final Map<Integer, CompletableFuture<Object>> requestMap = new ConcurrentHashMap<>();

    // 构造函数，接收WebSocket URL并连接
    public CdpClient(String websocketUrl){
        this.webSocketClient = new WebSocketClient(HttpUtil.parseUri(websocketUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
                log.info("WebSocket connect success.");
            }

            @Override
            public void onMessage(String message) {
                log.info("WebSocket receive message: " + message);
                handleMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("WebSocket connect close: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                log.error("WebSocket error: " + ex.getMessage(), ex);
            }
        };
        // 连接 WebSocket
        try {
            this.webSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 处理接收到的消息
    private void handleMessage(String message) {
        // 解析消息，获取对应的请求ID
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> response = JsonUtil.fromJsonStr(message, mapType);
        Integer idStr = ((Double)response.get("id")).intValue();

        CompletableFuture<Object> future = requestMap.get(idStr);

        // 如果找到对应的Future，完成它并从map中移除
        if (future != null) {
            future.complete(message);
            requestMap.remove(idStr);
        }
    }

    // 发送CDP指令，执行JavaScript代码并返回结果 (异步)
    public CompletableFuture<Object> executeCommand(String jsCode) {
        Integer requestId = FactorUtil.randomInteger(8);

        // 构造请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("expression", jsCode);

        Map<String, Object> command = new HashMap<>();
        command.put("id", requestId);  // 使用UUID作为请求ID
        command.put("method", "Runtime.evaluate");
        command.put("params", params);

        // 创建一个CompletableFuture并放入map中
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();
        requestMap.put(requestId, resultFuture);

        // 发送消息
        webSocketClient.send(JsonUtil.toJsonStr(command));

        // 返回 future，调用方可以等待结果
        return resultFuture;
    }

    public Object aexec(String jsCode){
        return aexec(jsCode, 5);
    }

    // 同步执行CDP指令，等待执行完成并返回结果
    public Object aexec(String jsCode, int timeout) {
        // 调用异步的 executeCommand 函数，等待并返回结果
        CompletableFuture<Object> future = executeCommand(jsCode);
        try {
            return future.get(timeout, TimeUnit.SECONDS);  // 等待执行完成
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    // 关闭WebSocket连接
    public void close() {
        webSocketClient.close();
    }
}
