package org.m2sec.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.m2sec.core.enums.LogLevel;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:18
 * @description:
 */
@Getter
@ToString
@AllArgsConstructor
public class Setting {
    private LogLevel logLevel;
    private String staticExtensions;

    public String[] getStaticExtensions() {
        return staticExtensions.split("\\|");
    }
}
