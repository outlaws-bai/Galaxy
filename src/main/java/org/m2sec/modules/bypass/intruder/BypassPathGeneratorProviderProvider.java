package org.m2sec.modules.bypass.intruder;

import burp.api.montoya.intruder.AttackConfiguration;
import org.m2sec.burp.intruder.AbstractPayloadGeneratorProvider;
import org.m2sec.common.utils.BurpUtil;
import org.m2sec.modules.bypass.BypassTools;

import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BypassPathGeneratorProviderProvider extends AbstractPayloadGeneratorProvider {

    @Override
    public String displayName() {
        return "BypassPath";
    }

    @Override
    public Set<String> generatePayloadSet(AttackConfiguration attackConfiguration) {
        byte[] message = attackConfiguration.requestTemplate().content().getBytes();
        String currentPath = BurpUtil.getIntruderWrappedText(message);
        return BypassTools.generateBypassPathPayloads(currentPath);
    }
}
