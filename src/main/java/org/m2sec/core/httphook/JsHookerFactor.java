package org.m2sec.core.httphook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Option;
import org.m2sec.core.common.ReflectTools;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:49
 * @description:
 */
@Slf4j
@Getter
public class JsHookerFactor extends IHttpHooker {


    Invocable invocable;

    @Override
    public void init(Option opt) {
        option = opt;
        String filepath = FileTools.getExampleScriptFilePath(option.getCodeSelectItem(),
            Constants.JS_FILE_SUFFIX);
        init(filepath);

    }

    public void init(String filepath) {
        try {
            String content = FileTools.readFileAsString(filepath);
            ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine(this.getClass().getClassLoader());
            engine.eval(content);
            invocable = (Invocable) engine;
            safeRun("set_log", log);
        } catch (ScriptException e) {
            throw new RuntimeException("load java script fail.", e);
        }
        log.info("load java script file success. {}", filepath);
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        return (Request) safeRun(Constants.HOOK_FUNC_1, request);
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return (Request) safeRun(Constants.HOOK_FUNC_2, request);
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return (Response) safeRun(Constants.HOOK_FUNC_3, response);
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return (Response) safeRun(Constants.HOOK_FUNC_4, response);
    }

    @Override
    public void destroy() {
        invocable = null;
    }

    public Object safeRun(String funcName, Object arg) {
        try {
            return invocable.invokeFunction(ReflectTools.camelToSnake(funcName), arg);
        } catch (NoSuchMethodException e) {
            log.warn("You have not implemented the {} method, which may lead to unknown issues", funcName);
            return null;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

}
