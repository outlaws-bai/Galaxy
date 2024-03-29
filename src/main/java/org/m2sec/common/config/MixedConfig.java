package org.m2sec.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MixedConfig {


    private SqlMapConfig sqlmap;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class SqlMapConfig {
        private String path;
        private String arg;

        public static SqlMapConfig getDefault() {
            return new SqlMapConfig("", "");
        }
    }

    public static MixedConfig getDefault() {
        return new MixedConfig(SqlMapConfig.getDefault());
    }

}
