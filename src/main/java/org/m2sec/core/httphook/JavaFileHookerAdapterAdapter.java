package org.m2sec.core.httphook;

import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Option;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.ReflectTools;
import org.m2sec.core.dynamic.IJavaHooker;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.slf4j.Logger;


/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class JavaFileHookerAdapterAdapter extends IHttpHooker implements ICodeHookerAdapter {

    private IJavaHooker hooker;

    @Override
    public void init(Option opt) {
        option = opt;
        String javaFilePath = FileTools.getExampleScriptFilePath(option.getJavaSelectItem(),
            Constants.JAVA_FILE_SUFFIX);
        init(javaFilePath);
        log.info("load java file success. {}", javaFilePath);
    }

    public void init(String javaFilePath) {
        Class<?> clazz;
        if (javaFilePath.endsWith(Constants.JAVA_FILE_SUFFIX)) clazz = ReflectTools.loadJavaFile(javaFilePath);
        else if (javaFilePath.endsWith(Constants.JAVA_COMPILED_FILE_SUFFIX)) clazz =
            ReflectTools.loadJavaClass(javaFilePath);
        else throw new IllegalArgumentException("javaFilePath suffix error!");
        hooker = (IJavaHooker) ReflectTools.newInstance(clazz, Logger.class, log);
    }


    @Override
    public Request hookRequestToBurp(Request request) {
        return hooker.hookRequestToBurp(request);
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return hooker.hookRequestToServer(request);
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return hooker.hookResponseToBurp(response);
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return hooker.hookResponseToClient(response);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return hooker.encrypt(data);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return hooker.decrypt(data);
    }

    public IJavaHooker getHooker() {
        return hooker;
    }

    @Override
    public void destroy() {
        hooker = null;
    }

}