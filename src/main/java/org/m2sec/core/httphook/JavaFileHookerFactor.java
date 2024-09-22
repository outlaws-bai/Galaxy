package org.m2sec.core.httphook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.*;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.slf4j.Logger;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
@Getter
public class JavaFileHookerFactor extends IHttpHooker {

    private Class<?> clazz;

    private Object hooker;

    private static final HttpHookService SERVICE = HttpHookService.JAVA;

    @Override
    public void init(Config config1) {
        config = config1;
        option = config1.getOption();
        String filepath =
            Constants.HTTP_HOOK_EXAMPLES_DIR + File.separator + SERVICE.getDir() + File.separator + option.getCodeSelectItem();
        init(filepath);
    }

    public void init(String javaFilePath) {
        if (javaFilePath.endsWith(Constants.JAVA_FILE_SUFFIX)) clazz = ReflectTools.loadJavaFile(javaFilePath);
        else if (javaFilePath.endsWith(Constants.JAVA_COMPILED_FILE_SUFFIX)) clazz =
            ReflectTools.loadJavaClass(javaFilePath);
        else throw new IllegalArgumentException("javaFilePath suffix error!");
        hooker = ReflectTools.newInstance(clazz, Logger.class, log);
        log.info("load java file success. {}", javaFilePath);
    }


    @Override
    public Request hookRequestToBurp(Request request) {
        return (Request) ReflectTools.callFunc(clazz, hooker, Constants.HOOK_FUNC_1, Request.class, request);
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return (Request) ReflectTools.callFunc(clazz, hooker, Constants.HOOK_FUNC_2, Request.class, request);
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return (Response) ReflectTools.callFunc(clazz, hooker, Constants.HOOK_FUNC_3, Response.class, response);
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return (Response) ReflectTools.callFunc(clazz, hooker, Constants.HOOK_FUNC_4, Response.class, response);
    }

    @Override
    public void destroy() {
        clazz = null;
        hooker = null;
    }

}

