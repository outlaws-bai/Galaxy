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

    public List<IPayloadProvider> getProviders() {
        List<IPayloadProvider> retVal = new ArrayList<>();
        retVal.add(new BypassAuthOfPathProvider(api));
        retVal.add(new BypassUrlProviderProvider(api));
        return retVal;
    }
}
