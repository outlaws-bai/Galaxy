package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.core.utils.FactorUtil;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class HttpHookCodeTest {

    public static final String examplesFilePath = "./src/main/resources/examples";

    @BeforeAll
    public static void setRootLoggerLevel() {
        System.setProperty("python.import.site", "false");
    }


    @Test
    public void testOneCodeHooker() {
        testCodeHooker(examplesFilePath + File.separator + "Sm4.java");
    }



    @Test
    public void testAllCodeHooker() {
        for (String filepath : FileTools.listDir(examplesFilePath)) {
            try {
                testCodeHooker(filepath);
            } catch (Exception e) {
                log.error("test error: {}", filepath, e);
            }
        }
    }

    public void testCodeHooker(String filepath) {
        String randomString1 = FactorUtil.randomString(50);
        String randomString2 = FactorUtil.randomString(50);

        Request request = Request.of("https://www.baidu.com/a/b/c", Method.POST);
        request.setContent(("{\"data\": \"" + randomString1 + "\"}").getBytes());
        Response response = Response.empty();
        response.setContent(("{\"data\": \"" + randomString2 + "\"}").getBytes());
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString1);
        IHttpHooker hooker = getCodeHooker(filepath);

        log.info("raw request: \r\n{}", request);
        Request request1 = hooker.hookRequestToServer(request);
        log.info("encrypted request: \r\n{}", request1);
        Request request2 = hooker.hookRequestToBurp(request1);
        log.info("decrypted request: \r\n{}", request2);

        log.info("raw response: \r\n{}", response);
        Response response1 = hooker.hookResponseToClient(response);
        log.info("encrypted response: \r\n{}", response1);
        Response response2 = hooker.hookResponseToBurp(response);
        log.info("decrypted response: \r\n{}", response2);
    }


    private static IHttpHooker getCodeHooker(String filepath) {
        IHttpHooker hooker;
        if (filepath.endsWith(Constants.JAVA_FILE_SUFFIX)) {
            JavaFileHookerFactor hookerFactor = new JavaFileHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else if (filepath.endsWith(Constants.PYTHON_FILE_SUFFIX)) {
            PythonHookerFactor hookerFactor = new PythonHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else if (filepath.endsWith(Constants.JS_FILE_SUFFIX)) {
            JsHookerFactor hookerFactor = new JsHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else {
            throw new RuntimeException("Abnormal example file");
        }
        return hooker;
    }



}
