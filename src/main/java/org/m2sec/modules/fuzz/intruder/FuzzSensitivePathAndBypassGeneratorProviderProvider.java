package org.m2sec.modules.fuzz.intruder;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class FuzzSensitivePathAndBypassGeneratorProviderProvider extends FuzzSensitivePathGeneratorProviderProvider {
    @Override
    public String displayName() {
        return "FuzzSensitivePathAndBypass";
    }

    @Override
    public boolean isBypass() {
        return true;
    }
}
