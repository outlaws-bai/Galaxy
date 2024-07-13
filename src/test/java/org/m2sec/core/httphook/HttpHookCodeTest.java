package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Helper;
import org.m2sec.core.dynamic.ICodeHooker;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Request;
import org.m2sec.core.utils.CodeUtil;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class HttpHookCodeTest {

    public static final String examplesFilePath = "./src/main/resources//examples";

    @BeforeAll
    public static void setRootLoggerLevel() {
        Helper.initAndLoadConfig(null);
        System.setProperty("python.import.site", "false");
    }

    @Test
    public void testOneCodeHookerByRandomString() {
        testCodeHookerByRandomString(examplesFilePath + File.separator + "aes_cbc.py");
    }

    @Test
    public void testOneCodeHookerByRequest() {
        testCodeHookerByRequest(examplesFilePath + File.separator + "aes_cbc.py");
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
        ICodeHooker hooker = getCodeHooker(filepath);
        log.info("raw request: \r\n{}", request);
        Request request1 = hooker.hookRequestToServer(request);
        log.info("encrypted request: \r\n{}", request1);
        Request request2 = hooker.hookRequestToBurp(request1);
        log.info("decrypted request: \r\n{}", request2);
    }

    public void testCodeHookerByRandomString(String filepath) {
        String randomString = Helper.generateRandomString(50);
        log.info("hook by file: {}", filepath);
        log.info("randomString: {}", randomString);
        ICodeHooker hooker = getCodeHooker(filepath);
        byte[] encryptData = hooker.encrypt(randomString.getBytes());
        log.info("encrypted base64 data: {}", CodeUtil.b64encodeToString(encryptData));
        byte[] data = hooker.decrypt(encryptData);
        log.info("decrypted data: {}", new String(data));
    }

    private static ICodeHooker getCodeHooker(String filepath) {
        ICodeHooker hooker;
        if (filepath.endsWith(Constants.JAVA_FILE_SUFFIX)) {
            JavaFileHookerFactor hookerFactor = new JavaFileHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor.getHooker();
        } else if (filepath.endsWith(Constants.PYTHON_FILE_SUFFIX)) {
            PythonHookerFactor hookerFactor = new PythonHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor.getHooker();
        } else if (filepath.endsWith(Constants.JS_FILE_SUFFIX)) {
            JsHookerFactor hookerFactor = new JsHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor.getHooker();
        } else {
            throw new RuntimeException("Abnormal example file");
        }
        return hooker;
    }



}
