package org.m2sec.burp.intruder;

import burp.api.montoya.intruder.AttackConfiguration;
import burp.api.montoya.intruder.PayloadGenerator;
import burp.api.montoya.intruder.PayloadGeneratorProvider;
import org.m2sec.common.Log;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description: 抽象payload生成器
 */
public abstract class AbstractPayloadGeneratorProvider implements PayloadGeneratorProvider {

    private static final Log log = new Log(AbstractPayloadGeneratorProvider.class);

    @Override
    public PayloadGenerator providePayloadGenerator(AttackConfiguration attackConfiguration) {
        Set<String> payloads = generatePayloadSet(attackConfiguration);
        log.debug("payloads size %d.", payloads.size());
        return new CommonPayloadGenerator(new ArrayList<>(payloads));
    }

    public abstract Set<String> generatePayloadSet(AttackConfiguration attackConfiguration);
}
