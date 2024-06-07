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
public class CloudConfig {

    private AwsConfig awsConfig;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class AwsConfig {
        private String service;
        private String ak;
        private String sk;
        private String token;
        private String region;

        public static AwsConfig getDefault() {
            return new AwsConfig("s3", "", "", "", "");
        }
    }

    public static CloudConfig getDefault() {
        return new CloudConfig(AwsConfig.getDefault());
    }
}
