package org.m2sec.abilities.payloads;

import burp.api.montoya.MontoyaApi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 11:51
 * @description:
 */

public class MasterPayloadGeneratorProviderFactor {

    private final MontoyaApi api;

    public MasterPayloadGeneratorProviderFactor(MontoyaApi api) {
        this.api = api;
    }

    public List<IPayloadGeneratorProvider> getProviders() {
        List<IPayloadGeneratorProvider> retVal = new ArrayList<>();
        retVal.add(new BypassPathGeneratorProviderProvider(api));
        retVal.add(new BypassUrlGeneratorProviderProvider(api));
        return retVal;
    }
}
