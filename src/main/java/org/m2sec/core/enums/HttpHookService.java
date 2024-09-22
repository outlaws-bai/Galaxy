package org.m2sec.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@AllArgsConstructor
public enum HttpHookService {
    GRPC(null, null),
    HTTP(null, null),
    JAVA("java", ".java"),
    GRAALPY("graalpy", ".py"),
    JS("js", ".js"),
    JYTHON("jython", ".py");

    private final String dir;
    private final String suffix;

}
