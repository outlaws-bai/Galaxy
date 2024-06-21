package org.m2sec.modules.fuzz.menu;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
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
