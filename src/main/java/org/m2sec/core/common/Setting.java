package org.m2sec.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.core.enums.LogLevel;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:18
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Setting {
    private LogLevel logLevel;
    private String staticExtensions;
    private boolean parsedSwaggerApiDocRequestAutoSend;
    private String sqlmapExecutePath;
    private String sqlmapExecuteArgs;
}
