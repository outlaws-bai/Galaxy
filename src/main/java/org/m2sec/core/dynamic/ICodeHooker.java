package org.m2sec.core.dynamic;

import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 23:13
 * @description:
 */

public interface ICodeHooker {

    Request hookRequestToBurp(Request request);

    Request hookRequestToServer(Request request);

    Response hookResponseToBurp(Response response);

    Response hookResponseToClient(Response response);

    byte[] decrypt(byte[] content);

    byte[] encrypt(byte[] content);
}
