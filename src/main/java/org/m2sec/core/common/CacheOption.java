package org.m2sec.core.common;

import lombok.*;
import lombok.experimental.Accessors;
import org.m2sec.core.enums.HttpHookService;

/**
 * @author: outlaws-bai
 * @date: 2024/7/10 13:45
 * @description:
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CacheOption {
    private HttpHookService hookWay;
    private String requestCheckExpression;
    private boolean hookRequest;
    private boolean hookResponse;
    private String grpcConn;
    private String javaSelectItem;
    private boolean hookStart;


}