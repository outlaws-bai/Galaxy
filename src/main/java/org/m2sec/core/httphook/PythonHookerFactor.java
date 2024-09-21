package org.m2sec.core.httphook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Helper;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:49
 * @description:
 */
@Slf4j
@Getter
public class PythonHookerFactor extends IHttpHooker {

    private Context context;
    private Value bind;
    private Value func1;
    private Value func2;
    private Value func3;
    private Value func4;


    @Override
    public void init(Config config1) {
        config = config1;
        option = config1.getOption();
        String filepath = FileTools.getExampleScriptFilePath(option.getCodeSelectItem(), Constants.PYTHON_FILE_SUFFIX);
        init(filepath);
    }

    public void init(String filepath) {
        Context.Builder builder = Context.newBuilder("python").allowExperimentalOptions(true)
            .allowHostAccess(HostAccess.ALL)
            .allowAllAccess(true)
            .hostClassLoader(getClass().getClassLoader());
        context = builder.build();
        bind = context.getBindings("python");
        context.eval("python", FileTools.readFileAsString(filepath));
        init();
        log.info("load python file success. {}", filepath);
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        if (func1 == null) return null;
        return func1.execute(request).as(Request.class);
    }

    @Override
    public Request hookRequestToServer(Request request) {
        if (func2 == null) return null;
        return func2.execute(request).as(Request.class);
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        if (func3 == null) return null;
        return func3.execute(response).as(Response.class);
    }

    @Override
    public Response hookResponseToClient(Response response) {
        if (func4 == null) return null;
        return func4.execute(response).as(Response.class);
    }

    @Override
    public void destroy() {
        bind = null;
        func1 = null;
        func2 = null;
        func3 = null;
        func4 = null;
        context.getEngine().close();
        context.close();
    }

    private void init() {
        Value setLog = safeGetFunction("set_log");
        if (setLog != null) setLog.execute(log);
        func1 = safeGetFunction(Constants.HOOK_FUNC_1);
        func2 = safeGetFunction(Constants.HOOK_FUNC_2);
        func3 = safeGetFunction(Constants.HOOK_FUNC_3);
        func4 = safeGetFunction(Constants.HOOK_FUNC_4);
    }

    private Value safeGetFunction(String funcName) {
        Value value = bind.getMember(Helper.camelToSnake(funcName));
        if (value == null) {
            log.warn("You have not implemented the {} method, which may lead to unknown issues", funcName);
            return null;
        }
        return value;
    }
}
