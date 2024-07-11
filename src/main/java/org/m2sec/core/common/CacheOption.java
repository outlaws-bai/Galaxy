package org.m2sec.core.common;

import lombok.*;
import org.m2sec.core.enums.HttpHookWay;

import java.io.File;

/**
 * @author: outlaws-bai
 * @date: 2024/7/10 13:45
 * @description:
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CacheOption {
    private HttpHookWay hookWay;
    private String requestCheckExpression;
    private boolean hookRequest;
    private boolean hookResponse;
    private String grpcConn;
    private String javaSelectItem;
    private boolean hookStart;

    public String getScriptPath(String item, String suffix) {
        return Constants.HTTP_HOOK_EXAMPLES_DIR + File.separator + item + suffix;
    }

}