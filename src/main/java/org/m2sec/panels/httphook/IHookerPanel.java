package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.abilities.MasterHttpHandler;
import org.m2sec.abilities.MaterProxyHandler;
import org.m2sec.core.common.Option;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.httphook.IHttpHooker;

import javax.swing.*;
import java.nio.file.attribute.AttributeView;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:32
 * @description:
 */

public abstract class IHookerPanel<T extends IHttpHooker> extends JPanel {

    protected final Option option;

    protected final MontoyaApi api;

    public IHookerPanel(Option option, MontoyaApi api) {
        this.option = option;
        this.api = api;
    }

    public void start(Option option){
        T hooker = newHooker();
        hooker.init(option);
        MasterHttpHandler.hooker = hooker;
        MaterProxyHandler.hooker = hooker;
    }

    public void stop(Option option){
        option.setHookStart(false);
        IHttpHooker hooker = MasterHttpHandler.hooker;
        hooker.destroy();
        MasterHttpHandler.hooker = null;
        MaterProxyHandler.hooker = null;
    }

    public abstract T newHooker();

    public abstract String getInput();

    public abstract void resetInput();
}
