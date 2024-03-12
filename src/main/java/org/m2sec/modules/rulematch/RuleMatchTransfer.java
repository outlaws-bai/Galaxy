package org.m2sec.modules.rulematch;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import org.m2sec.common.Tuple;
import org.m2sec.common.models.FuzzDict;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;

import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class RuleMatchTransfer {

    public static Annotations matchRequest(Request request) {
        Set<String> params = FuzzDict.getParams(new Tuple<>(request, null));
        if (params.contains("url")) {
            return Annotations.annotations(HighlightColor.RED);
        }
        return Annotations.annotations();
    }

    public static Annotations matchResponse(Response response) {
        if (new String(response.getContent()).contains("swagger")) {
            return Annotations.annotations(HighlightColor.RED);
        }
        return Annotations.annotations();
    }
}
