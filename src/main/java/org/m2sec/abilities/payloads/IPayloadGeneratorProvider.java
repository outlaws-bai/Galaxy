package org.m2sec.abilities.payloads;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.intruder.AttackConfiguration;
import burp.api.montoya.intruder.PayloadGenerator;
import burp.api.montoya.intruder.PayloadGeneratorProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 11:50
 * @description:
 */
@Slf4j
public abstract class IPayloadGeneratorProvider implements PayloadGeneratorProvider {

    private final MontoyaApi api;

    public IPayloadGeneratorProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public PayloadGenerator providePayloadGenerator(AttackConfiguration attackConfiguration) {
        Set<String> payloads = generatePayloadSet(attackConfiguration);
        log.info("payloads size {}.", payloads.size());
        return new CommonPayloadGenerator(new ArrayList<>(payloads));
    }


    public abstract Set<String> generatePayloadSet(AttackConfiguration attackConfiguration);

}
