package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Option;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.httphook.IHttpHooker;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:38
 * @description:
 */

public class EmptyHookerPanel extends IHookerPanel<IHttpHooker> {

    public EmptyHookerPanel(Option option, MontoyaApi api) {
        super(option, api);
    }

    @Override
    public IHttpHooker newHooker() {
        return null;
    }

    @Override
    public String getInput() {
        return null;
    }

    @Override
    public void resetInput() {

    }
}
