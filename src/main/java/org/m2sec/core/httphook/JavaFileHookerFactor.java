package org.m2sec.core.httphook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Option;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.ReflectTools;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.slf4j.Logger;

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

    @Override
    public void init(Option opt) {
        option = opt;
        String javaFilePath = FileTools.getExampleScriptFilePath(option.getCodeSelectItem(),
            Constants.JAVA_FILE_SUFFIX);
        init(javaFilePath);
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

