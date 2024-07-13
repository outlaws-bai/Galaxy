package org.m2sec.core.httphook;

import org.m2sec.core.common.Option;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:49
 * @description:
 */

public class PythonHookerFactor extends IHttpHooker implements ICodeHookerFactor {
    @Override
    public void init(Option opt) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Request hookRequestToBurp(Request request) {
        return null;
    }

    @Override
    public Request hookRequestToServer(Request request) {
        return null;
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        return null;
    }

    @Override
    public Response hookResponseToClient(Response response) {
        return null;
    }

    @Override
    public void init(String filepath) {

    }

    @Override
    public byte[] encrypt(byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return new byte[0];
    }
}
