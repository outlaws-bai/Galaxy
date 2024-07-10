package org.m2sec.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author: outlaws-bai
 * @date: 2024/7/10 13:45
 * @description:
 */
@Getter
@ToString
@AllArgsConstructor
public class CacheInfo {
    private String requestCheckExpression;
    private boolean hookRequest;
    private boolean hookResponse;
    private String rpcConn;
    private String javaSelectItem;
}