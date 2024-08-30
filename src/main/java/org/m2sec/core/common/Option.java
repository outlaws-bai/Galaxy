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
public class Option {
    private HttpHookService hookService;
    private String requestCheckExpression;
    private boolean hookResponse;
    private boolean autoForwardRequest;
    private String grpcConn;
    private String codeSelectItem;
    private boolean hookStart;
}