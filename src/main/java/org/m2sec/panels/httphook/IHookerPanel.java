package org.m2sec.panels.httphook;

import org.m2sec.abilities.MasterHttpHandler;
import org.m2sec.abilities.MaterProxyHandler;
import org.m2sec.core.common.Option;
import org.m2sec.core.httphook.IHttpHooker;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:32
 * @description:
 */

public abstract class IHookerPanel<T extends IHttpHooker> extends JPanel {

    public IHookerPanel() {
        setName(displayName());
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

    public abstract String displayName();
}
