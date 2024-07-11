package org.m2sec.panels.httphook;

import org.m2sec.core.httphook.AbstractHttpHooker;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:38
 * @description:
 */

public class EmptyImpl extends IHookService<AbstractHttpHooker> {

    @Override
    public AbstractHttpHooker newHooker() {
        return null;
    }

    @Override
    public String displayName() {
        return "...";
    }
}
