package org.m2sec.modules.fuzz.intruder;

import burp.api.montoya.intruder.AttackConfiguration;
import org.m2sec.burp.intruder.AbstractPayloadGeneratorProvider;
import org.m2sec.common.Constants;
import org.m2sec.common.utils.BurpUtil;
import org.m2sec.common.utils.FileUtil;
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
        return generatePayloadSet(originPath, isBypass());
    }

    public static Set<String> generatePayloadSet(String originPath, boolean isBypass) {
        Set<String> payloads = new HashSet<>();
        originPath = HttpUtil.normalizePath(originPath);
        String[] parts = originPath.split("/");
        for (String sensitivePath :
                FileUtil.readFileAsStringArray(Constants.FUZZ_SENSITIVE_PATH_DICT_FILE_PATH)) {
            payloads.add(sensitivePath);
            if (parts.length > 2) {
                for (int i = 1; i < parts.length - 1; i++) {
                    String partSensitivePath =
                            String.join("/", Arrays.copyOfRange(parts, 0, i + 1)) + sensitivePath;
                    if (isBypass) {
                        payloads.addAll(BypassTools.generateBypassPathPayloads(partSensitivePath));
                    } else {
                        payloads.add(partSensitivePath);
                    }
                }
            }
        }
        return payloads;
    }

    public boolean isBypass() {
        return false;
    }
}
