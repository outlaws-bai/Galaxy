package org.m2sec.modules.fuzz.intruder;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class FuzzSensitivePathAndBypassGeneratorProviderProvider
        extends FuzzSensitivePathGeneratorProviderProvider {
    @Override
    public String displayName() {
        return "Fuzz Sensitive Path And Bypass";
    }

    @Override
    public boolean isBypass() {
        return true;
    }
}
