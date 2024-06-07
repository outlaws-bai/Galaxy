package org.m2sec.modules.fuzz.menu;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class FuzzSensitivePathAndBypassMenuItem extends FuzzSensitivePathMenuItem {

    @Override
    public String displayName() {
        return "FuzzSensitivePathAndBypass";
    }

    @Override
    public boolean isBypass() {
        return true;
    }
}
