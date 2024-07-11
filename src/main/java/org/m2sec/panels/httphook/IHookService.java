package org.m2sec.panels.httphook;

import org.m2sec.abilities.MasterHttpHandler;
import org.m2sec.abilities.MaterProxyHandler;
import org.m2sec.core.common.CacheOption;
import org.m2sec.core.httphook.AbstractHttpHooker;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/11 20:32
 * @description:
 */

public abstract class IHookService<T extends AbstractHttpHooker> extends JPanel {

    public IHookService() {
        setName(displayName());
    }

    public void start(CacheOption cache){
        T hooker = newHooker();
        hooker.init(cache);
        MasterHttpHandler.hooker = hooker;
        MaterProxyHandler.hooker = hooker;
    }

    public void stop(CacheOption cache){
        cache.setHookStart(false);
        AbstractHttpHooker hooker = MasterHttpHandler.hooker;
        hooker.destroy();
        MasterHttpHandler.hooker = null;
        MaterProxyHandler.hooker = null;
    }

    public abstract T newHooker();

    public abstract String displayName();
}
