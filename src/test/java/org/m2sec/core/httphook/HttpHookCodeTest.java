package org.m2sec.core.httphook;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Helper;
import org.m2sec.core.dynamic.IJavaHooker;
import org.m2sec.core.enums.LogLevel;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Request;
import org.m2sec.core.utils.CodeUtil;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class HttpHookCodeTest {

    public static final int rpcPort = 8443;

    public static final String examplesFilePath = "./src/main/resources//examples";

    @BeforeAll
    public static void setRootLoggerLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("root");
        rootLogger.setLevel(Level.valueOf(LogLevel.INFO.name()));
    }

    @Test
    public void testOneCodeHookerByRandomString() {
        testCodeHookerByRandomString(examplesFilePath + File.separator + "Sm2.java");
    }

    @Test
    public void testOneCodeHookerByRequest() {
        testCodeHookerByRequest(examplesFilePath + File.separator + "Sm2.java");
    }


    @Test
    public void testAllCodeHookerByRandomString() {
        for (String filepath : FileTools.listDir(examplesFilePath)) {
            try {
                testCodeHookerByRandomString(filepath);
            } catch (Exception e) {
                log.error("test error: {}", filepath, e);
            }
        }
    }

    @Test
    public void testAllCodeHookerByRequest() {
        for (String filepath : FileTools.listDir(examplesFilePath)) {
            try {
                testCodeHookerByRequest(filepath);
            } catch (Exception e) {
                log.error("test error: {}", filepath, e);
            }
        }
    }

    public void testCodeHookerByRequest(String filepath) {
        String randomString = Helper.generateRandomString(50);
        Request request = Request.of("https://www.baidu.com/a/b/c", Method.POST);
        request.setContent(("{\"data\": \"" + randomString + "\"}").getBytes());
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString);
        ICodeHookerAdapter hooker = getCodeHooker(filepath);
        IJavaHooker hooker1 = ((JavaFileHookerAdapterAdapter) hooker).getHooker();
        log.info("raw request: \r\n{}", request);
        Request request1 = hooker1.hookRequestToServer(request);
        log.info("encrypted request: \r\n{}", request1);
        Request request2 = hooker1.hookRequestToBurp(request1);
        log.info("decrypted request: \r\n{}", request2);
    }

    public void testCodeHookerByRandomString(String filepath) {
        String randomString = Helper.generateRandomString(50);
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString);
        ICodeHookerAdapter hooker = getCodeHooker(filepath);
        byte[] encryptData = hooker.encrypt(randomString.getBytes());
        log.info("encrypted base64 data: {}", CodeUtil.b64encodeToString(encryptData));
        byte[] data = hooker.decrypt(encryptData);
        log.info("decrypted data: {}", new String(data));
    }

    private static ICodeHookerAdapter getCodeHooker(String filepath) {
        ICodeHookerAdapter hooker;
        if (filepath.endsWith(Constants.JAVA_FILE_SUFFIX)) {
            hooker = new JavaFileHookerAdapterAdapter();
        } else if (filepath.endsWith(Constants.PYTHON_FILE_SUFFIX)) {
            hooker = new PythonHookerAdapter();
        } else if (filepath.endsWith(Constants.JS_FILE_SUFFIX)) {
            hooker = new JsHookerAdapter();
        } else {
            throw new RuntimeException("Abnormal example file");
        }
        ICodeHookerAdapter codeHooker = hooker;
        codeHooker.init(filepath);
        return codeHooker;
    }



}
