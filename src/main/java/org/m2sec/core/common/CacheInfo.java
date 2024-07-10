package org.m2sec.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.m2sec.core.enums.HttpHookWay;

/**
 * @author: outlaws-bai
 * @date: 2024/7/10 13:45
 * @description:
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class CacheInfo {
    private HttpHookWay hookWay;
    private String requestCheckExpression;
    private boolean hookRequest;
    private boolean hookResponse;
    private String rpcConn;
    private String javaSelectItem;
}