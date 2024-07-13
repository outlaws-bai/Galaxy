package org.m2sec.core.httphook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Option;
import org.m2sec.core.dynamic.ICodeHooker;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:49
 * @description:
 */
@Slf4j
@Getter
public class PythonHookerFactor extends IHttpHooker implements ICodeHookerFactor {

    private PythonInterpreter interpreter;

    private ICodeHooker hooker;

    public PythonHookerFactor() {
        interpreter = new PythonInterpreter();
    }

    @Override
    public void init(Option opt) {
        option = opt;
        String pyFilePath = FileTools.getExampleScriptFilePath(option.getCodeSelectItem(),
            Constants.PYTHON_FILE_SUFFIX);
        init(pyFilePath);
    }

    @Override
    public void init(String filepath) {
        interpreter.exec(FileTools.readFileAsString(filepath));
        PyObject object = interpreter.get("Hooker");
        String errorMessage = "load python script fail, \n" +
            "Unable to find a class named Hooker that inherits from ICodeHooker.";
        try {
            hooker = (ICodeHooker) (object.__call__(new PyObject[]{Py.java2py(log)})).__tojava__(ICodeHooker.class);
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
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

    @Override
    public void destroy() {
        interpreter.close();
        interpreter = null;
        hooker = null;
    }
}
