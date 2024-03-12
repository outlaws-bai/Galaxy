package org.m2sec.modules.fuzz.intruder;

import burp.api.montoya.intruder.AttackConfiguration;
import org.m2sec.burp.intruder.AbstractPayloadGeneratorProvider;
import org.m2sec.common.utils.BurpUtil;
import org.m2sec.common.utils.HttpUtil;
import org.m2sec.modules.bypass.BypassTools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class FuzzSensitivePathGeneratorProviderProvider extends AbstractPayloadGeneratorProvider {
    @Override
    public String displayName() {
        return "Fuzz Sensitive Path";
    }

    @Override
    public Set<String> generatePayloadSet(AttackConfiguration attackConfiguration) {
        byte[] message = attackConfiguration.requestTemplate().content().getBytes();
        String currentPath = BurpUtil.getIntruderWrappedText(message);
        return generatePayloadSet(currentPath);
    }

    public Set<String> generatePayloadSet(String originPath) {
        Set<String> payloadList = new HashSet<>();
        originPath = HttpUtil.normalizePath(originPath);
        String[] parts = originPath.split("/");
        for (String sensitivePath : SENSITIVE_PATH_ARRAY) {
            payloadList.add(sensitivePath);
            if (parts.length > 2) {
                for (int i = 1; i < parts.length - 1; i++) {
                    String partSensitivePath =
                            String.join("/", Arrays.copyOfRange(parts, 0, i + 1)) + sensitivePath;
                    if (isBypass()) {
                        payloadList.addAll(
                                BypassTools.generateBypassPathPayloads(partSensitivePath));
                    } else {

                        payloadList.add(partSensitivePath);
                    }
                }
            }
        }
        return payloadList;
    }

    public boolean isBypass() {
        return false;
    }

    public static final String[] SENSITIVE_PATH_ARRAY =
            new String[] {
                // swagger
                "/docs",
                "/redoc",
                "/openapi.json",
                "/api/docs",
                "/swagger",
                "/doc.html",
                "/swagger-doc",
                "/swagger-ui.html",
                "/swagger-resources",
                "/v2/api-docs",
                "/swagger-ui/index.html",
                "/v3/api-docs/swagger-config",
                "/v3/api-docs",
                "/api/__swagger__/",
                "/api/_swagger_/",
                "/api/apidocs/swagger.json",
                "/api/spec/swagger.json",
                "/api/swagger",
                "/api/swagger-ui.html",
                "/api/swagger.json",
                "/api/swagger.yaml",
                "/api/swagger.yml",
                "/api/swagger/index.html",
                "/api/swagger/static/index.html",
                "/api/swagger/swagger",
                "/api/swagger/ui/index",
                "/api/v1/swagger.json",
                "/api/v1/swagger.yaml",
                "/api/v2/swagger.json",
                "/api/v2/swagger.yaml",
                "/core/latest/swagger-ui/index.html",
                "/csp/gateway/slc/api/swagger-ui.html",
                "/docs/swagger.json",
                "/static/api/swagger.json",
                "/static/api/swagger.yaml",
                "/swagger-ui",
                "/swagger.json",
                "/swagger.yaml",
                "/swagger/api-docs",
                "/swagger/index.html",
                "/swagger/swagger",
                "/swagger/swagger-ui.htm",
                "/swagger/swagger-ui.html",
                "/swagger/ui",
                "/swagger/v1.0/api-docs",
                "/swagger/v1.0/swagger.json",
                "/swagger/v1.0/swagger.yaml",
                "/swagger/v1/api-docs",
                "/swagger/v1/swagger.json",
                "/swagger/v1/swagger.json/",
                "/swagger/v1/swagger.yaml",
                "/swagger/v2.0/api-docs",
                "/swagger/v2.0/swagger.json",
                "/swagger/v2.0/swagger.yaml",
                "/swagger/v2/api-docs",
                "/swagger/v2/swagger.json",
                "/swagger/v2/swagger.yaml",
                "/swagger/v3.0/api-docs",
                "/swagger/v3.0/swagger.json",
                "/swagger/v3.0/swagger.yaml",
                "/swaggerui",
                // actuator
                "/actuator",
                "/actuators",
                //  v1
                "/autoconfig",
                "/beans",
                "/caches",
                "/conditions",
                "/configprops",
                "/env",
                "/routes",
                "/gateway/routes",
                "/health",
                "/heapdump",
                "/info",
                "/logfile",
                "/loggers",
                "/mappings",
                "/metrics",
                "/prometheus",
                "/restart",
                "/scheduledtasks",
                "/shutdown",
                "/threaddump",
                "/trace",
                "/resume",
                "/pause",
                "/auditevents",
                "/auditLog",
                "/configurationMetadata",
                "/dump",
                "/events",
                "/exportRegisteredServices",
                "/features",
                "/flyway",
                "/healthcheck",
                "/integrationgraph",
                "/jolokia",
                "/liquibase",
                "/loggingConfig",
                "/refresh",
                "/registeredServices",
                "/releaseAttributes",
                "/resolveAttributes",
                "/sessions",
                "/springWebflow",
                "/sso",
                "/ssoSessions",
                "/statistics",
                "/status",
                "/httptrace",
                //  v2
                "/actuator/autoconfig",
                "/actuator/beans",
                "/actuator/caches",
                "/actuator/conditions",
                "/actuator/configprops",
                "/actuator/env",
                "/actuator/gateway/routes",
                "/actuator/health",
                "/actuator/heapdump",
                "/actuator/info",
                "/actuator/logfile",
                "/actuator/loggers",
                "/actuator/mappings",
                "/actuator/metrics",
                "/actuator/prometheus",
                "/actuator/restart",
                "/actuator/scheduledtasks",
                "/actuator/shutdown",
                "/actuator/threaddump",
                "/actuator/httptrace",
                "/actuator/resume",
                "/actuator/pause",
                "/actuator/auditevents",
                "/actuator/auditLog",
                "/actuator/trace",
                "/actuator/configurationMetadata",
                "/actuator/dump",
                "/actuator/events",
                "/actuator/exportRegisteredServices",
                "/actuator/features",
                "/actuator/flyway",
                "/actuator/healthcheck",
                "/actuator/integrationgraph",
                "/actuator/jolokia",
                "/actuator/liquibase",
                "/actuator/loggingConfig",
                "/actuator/refresh",
                "/actuator/registeredServices",
                "/actuator/releaseAttributes",
                "/actuator/resolveAttributes",
                "/actuator/sessions",
                "/actuator/springWebflow",
                "/actuator/sso",
                "/actuator/ssoSessions",
                "/actuator/statistics",
                "/actuator/status",
                "/actuator/httptrace",
                //   other
                "/actuators/dump",
                "/actuators/env",
                "/actuators/health",
                "/actuators/logfile",
                "/actuators/mappings",
                "/actuators/shutdown",
                "/actuators/trace",
                // druid
                "/druid/index.html"
                // custom
            };
}
