package org.m2sec.modules.bypass.intruder;

import burp.api.montoya.intruder.AttackConfiguration;
import org.m2sec.burp.intruder.AbstractPayloadGeneratorProvider;
import org.m2sec.common.models.Target;
import org.m2sec.common.utils.BurpUtil;
import org.m2sec.modules.bypass.BypassTools;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class BypassUrlGeneratorProviderProvider extends AbstractPayloadGeneratorProvider {

    @Override
    public String displayName() {
        return "Bypass Url";
    }

    @Override
    public Set<String> generatePayloadSet(AttackConfiguration attackConfiguration) {
        byte[] message = attackConfiguration.requestTemplate().content().getBytes();
        String selectUrl = BurpUtil.getIntruderWrappedText(message);
        try {
            URL evilUrl = new URL(selectUrl);
            if (attackConfiguration.httpService().isPresent()) {
                return BypassTools.generateBypassUrlPayloads(
                        Target.of(attackConfiguration.httpService().get()), evilUrl);
            } else {
                throw new RuntimeException("please input target.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("select text is incorrect url.");
        }
    }
}
