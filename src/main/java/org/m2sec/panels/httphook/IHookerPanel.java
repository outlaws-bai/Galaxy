package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import lombok.Getter;
import org.m2sec.abilities.MasterHttpHandler;
import org.m2sec.abilities.MasterProxyHandler;
import org.m2sec.core.common.Option;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.httphook.IHttpHooker;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:32
 * @description:
 */
public abstract class IHookerPanel<T extends IHttpHooker> extends JPanel {

    protected final Option option;

    protected final MontoyaApi api;
    @Getter
    protected final HttpHookService service;

    public IHookerPanel(Option option, MontoyaApi api, HttpHookService service) {
        this.option = option;
        this.api = api;
        this.service = service;
    }

    public void start(Option option){
        T hooker = newHooker();
        hooker.init(option);
        MasterHttpHandler.hooker = hooker;
        MasterProxyHandler.hooker = hooker;
    }

    public void stop(Option option){
        option.setHookStart(false);
        IHttpHooker hooker = MasterHttpHandler.hooker;
        hooker.destroy();
        MasterHttpHandler.hooker = null;
        MasterProxyHandler.hooker = null;
    }

    public abstract T newHooker();

    public abstract String getInput();

    public abstract void resetInput();
}
