package org.m2sec.core.httphook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.*;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:49
 * @description:
 */
@Slf4j
@Getter
public class JythonHookerFactor extends IHttpHooker {

    private PythonInterpreter interpreter;
    private PyFunction func1;
    private PyFunction func2;
    private PyFunction func3;
    private PyFunction func4;

    @Override
    public void init(Config config1) {
        config = config1;
        option = config1.getOption();
        String pyFilePath = FileTools.getExampleScriptFilePath(option.getCodeSelectItem(),
            Constants.JYTHON_FILE_SUFFIX);
        init(pyFilePath);
    }

    public void init(String filepath) {
        interpreter = new PythonInterpreter();
        interpreter.exec(FileTools.readFileAsString(filepath));
        try {
            interpreter.exec(FileTools.readFileAsString(filepath));
            init();
        } catch (Exception e) {
            throw new RuntimeException("load python script fail.", e);
        }
        log.info("load python script file success. {}", filepath);
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        if (func1 == null) return null;
        return (Request) func1.__call__(Py.java2py(request)).__tojava__(Request.class);
    }

    @Override
    public Request hookRequestToServer(Request request) {
        if (func2 == null) return null;
        return (Request) func2.__call__(Py.java2py(request)).__tojava__(Request.class);
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        if (func3 == null) return null;
        return (Response) func3.__call__(Py.java2py(response)).__tojava__(Response.class);
    }

    @Override
    public Response hookResponseToClient(Response response) {
        if (func4 == null) return null;
        return (Response) func4.__call__(Py.java2py(response)).__tojava__(Response.class);
    }

    @Override
    public void destroy() {
        interpreter.close();
        interpreter = null;
        func1 = null;
        func2 = null;
        func3 = null;
        func4 = null;
    }

    public void init() {
        PyFunction setLog = safeGetPyFunction("set_log");
        if (setLog != null) setLog.__call__(Py.java2py(log));
        func1 = safeGetPyFunction(Constants.HOOK_FUNC_1);
        func2 = safeGetPyFunction(Constants.HOOK_FUNC_2);
        func3 = safeGetPyFunction(Constants.HOOK_FUNC_3);
        func4 = safeGetPyFunction(Constants.HOOK_FUNC_4);

    }

    public PyFunction safeGetPyFunction(String funcName) {
        PyObject object = interpreter.get(Helper.camelToSnake(funcName));
        if (object == null) {
            log.warn("You have not implemented the {} method, which may lead to unknown issues", funcName);
            return null;
        }
        return (PyFunction) object;
    }
}
