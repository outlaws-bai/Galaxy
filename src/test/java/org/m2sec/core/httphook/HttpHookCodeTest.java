package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Helper;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.*;
import org.m2sec.core.utils.FactorUtil;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

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
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        System.setProperty("python.import.site", "false");
        Security.addProvider(new BouncyCastleProvider());
        Helper.checkDep();
    }

    @Test
    public void testOneCodeHooker() { // 仅测试json的示例
        HttpHookService service = HttpHookService.JYTHON;
        testCodeHooker(
                service,
                examplesFilePath
                        + File.separator
                        + service.getDir()
                        + File.separator
                        + "aes_cbc.py");
    }

    @Test
    public void testCodeHookers() { // 仅测试json的示例
        HttpHookService service = HttpHookService.JYTHON;
        List<String> failPaths = new ArrayList<>();
        for (String filepath :
                FileTools.listDir(examplesFilePath + File.separator + service.getDir())) {
            //noinspection CaughtExceptionImmediatelyRethrown
            try {
                testCodeHooker(service, filepath);
            } catch (AssertionError e) {
                throw e;
            } catch (Exception e) {
                failPaths.add(filepath);
                log.error("test error: {}", filepath, e);
            }
        }
        if (failPaths.isEmpty()) {
            log.info("all success");
        } else {
            failPaths.forEach(x -> log.error("fail: {}", x));
        }
    }

    @Test
    public void testQuery() { // 测试json外的query示例
        String randomString1 = FactorUtil.randomString(50);
        Request queryRequest = Request.of("https://www.baidu.com/a/b/c");
        queryRequest.setQuery(Query.of("username=" + randomString1));

        //        HttpHookService service = HttpHookService.JAVA;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "AesCbcForm.java";

        //        HttpHookService service = HttpHookService.JS;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "aes_cbc_query.js";

        HttpHookService service = HttpHookService.JYTHON;
        String filepath =
                examplesFilePath
                        + File.separator
                        + service.getDir()
                        + File.separator
                        + File.separator
                        + "aes_cbc_query.py";

        //        HttpHookService service = HttpHookService.JYTHON;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "aes_cbc_query.py";

        IHttpHooker hooker = getCodeHooker(service, filepath);
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString1);
        log.info("raw request: \r\n{}", queryRequest);
        Request request1 = hooker.hookRequestToServer(queryRequest);
        log.info("encrypted request: \r\n{}", request1);
        Request request2 = hooker.hookRequestToBurp(request1);
        log.info("decrypted request: \r\n{}", request2);
    }

    @Test
    public void testForm() { // 测试json外的form示例
        String randomString1 = FactorUtil.randomString(50);
        Request formRequest = Request.of("https://www.baidu.com/a/b/c");
        formRequest.setForm(Form.of("username=" + randomString1));

        //        HttpHookService service = HttpHookService.JAVA;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "AesCbcForm.java";

        //        HttpHookService service = HttpHookService.JS;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "aes_cbc_form.js";

        HttpHookService service = HttpHookService.JYTHON;
        String filepath =
                examplesFilePath
                        + File.separator
                        + service.getDir()
                        + File.separator
                        + File.separator
                        + "aes_cbc_form.py";

        //        HttpHookService service = HttpHookService.JYTHON;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "aes_cbc_form.py";

        IHttpHooker hooker = getCodeHooker(service, filepath);
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString1);
        log.info("raw request: \r\n{}", formRequest);
        Request request1 = hooker.hookRequestToServer(formRequest);
        log.info("encrypted request: \r\n{}", request1);
        Request request2 = hooker.hookRequestToBurp(request1);
        log.info("decrypted request: \r\n{}", request2);
    }

    @Test
    public void testFormData() { // 测试json外的form data示例
        String randomString1 = FactorUtil.randomString(50);
        Request formdataRequest = Request.of("https://www.baidu.com/a/b/c");
        FormData<Object> formData = new FormData();
        formData.put("username", randomString1);
        formdataRequest.setFormData(formData);

        //        HttpHookService service = HttpHookService.JAVA;
        //        String filepath =
        //            examplesFilePath + File.separator + service.getDir() + File.separator +
        // File.separator +
        //            "AesCbcFormData.java";

        //        HttpHookService service = HttpHookService.JS;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "aes_cbc_form_data.js";

        HttpHookService service = HttpHookService.JYTHON;
        String filepath =
                examplesFilePath
                        + File.separator
                        + service.getDir()
                        + File.separator
                        + File.separator
                        + "aes_cbc_form_data.py";

        //        HttpHookService service = HttpHookService.JYTHON;
        //        String filepath = examplesFilePath + File.separator + service.getDir() +
        // File.separator + File.separator +
        //            "aes_cbc_form_data.py";

        IHttpHooker hooker = getCodeHooker(service, filepath);
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString1);
        log.info("raw request: \r\n{}", formdataRequest);
        Request request1 = hooker.hookRequestToServer(formdataRequest);
        log.info("encrypted request: \r\n{}", request1);
        Request request2 = hooker.hookRequestToBurp(request1);
        log.info("decrypted request: \r\n{}", request2);
    }

    public void testCodeHooker(HttpHookService service, String filepath) {
        if (filepath.toLowerCase().contains("form") || filepath.toLowerCase().contains("query"))
            return;
        IHttpHooker hooker = getCodeHooker(service, filepath);
        String randomString1 = FactorUtil.randomString(50);
        String randomString2 = FactorUtil.randomString(50);

        Request request = Request.of("https://www.baidu.com/a/b/c", Method.POST);
        request.setContent(("{\"data\": \"" + randomString1 + "\"}").getBytes());
        Response response = Response.empty();
        response.setContent(("{\"data\": \"" + randomString2 + "\"}").getBytes());
        log.info("hook by java file: {}", filepath);
        log.info("randomString: {}", randomString1);

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
        hooker.destroy();
    }

    private static IHttpHooker getCodeHooker(HttpHookService service, String filepath) {
        assert !service.equals(HttpHookService.JS) || Constants.hasJs
                : "Due to compatibility issues, if you need to "
                        + "test JavaScript, please switch the "
                        + "graalService in build.gradle to js";
        assert !service.equals(HttpHookService.GRAALPY) || Constants.hasGraalpy
                : "Due to compatibility issues, if "
                        + "you need to test python, please switch"
                        + " the "
                        + "graalService in build.gradle to python";
        IHttpHooker hooker;
        if (service.equals(HttpHookService.JAVA)) {
            JavaFileHookerFactor hookerFactor = new JavaFileHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else if (service.equals(HttpHookService.GRAALPY)) {
            GraalpyHookerFactor hookerFactor = new GraalpyHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else if (service.equals(HttpHookService.JS)) {
            JsHookerFactor hookerFactor = new JsHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else if (service.equals(HttpHookService.JYTHON)) {
            JythonHookerFactor hookerFactor = new JythonHookerFactor();
            hookerFactor.init(filepath);
            hooker = hookerFactor;
        } else {
            throw new RuntimeException("Abnormal example file");
        }
        return hooker;
    }
}
