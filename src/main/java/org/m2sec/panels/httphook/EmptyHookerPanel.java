package org.m2sec.panels.httphook;

import org.m2sec.core.httphook.IHttpHooker;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:38
 * @description:
 */

public class EmptyHookerPanel extends IHookerPanel<IHttpHooker> {

    @Override
    public IHttpHooker newHooker() {
        return null;
    }

    @Override
    public String displayName() {
        return "...";
    }
}
