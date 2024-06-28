package org.m2sec.burp.intruder;

import burp.api.montoya.intruder.AttackConfiguration;
import burp.api.montoya.intruder.PayloadGenerator;
import burp.api.montoya.intruder.PayloadGeneratorProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description: 抽象payload生成器
 */
@Slf4j
public abstract class AbstractPayloadGeneratorProvider implements PayloadGeneratorProvider {


    @Override
    public PayloadGenerator providePayloadGenerator(AttackConfiguration attackConfiguration) {
        Set<String> payloads = generatePayloadSet(attackConfiguration);
        log.debug("payloads size {}.", payloads.size());
        return new CommonPayloadGenerator(new ArrayList<>(payloads));
    }

    public abstract Set<String> generatePayloadSet(AttackConfiguration attackConfiguration);
}
