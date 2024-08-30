package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Registration;
import lombok.Getter;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.Config;
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


    protected final Config config;

    protected final MontoyaApi api;
    @Getter
    protected final HttpHookService service;

    private Registration[] registrations;

    public IHookerPanel(Config config, MontoyaApi api, HttpHookService service) {
        this.config = config;
        this.api = api;
        this.service = service;
    }

    public void start(Config config) {
        T hooker = newHooker();
        hooker.init(config);
        HttpHookHandler.hooker = hooker;
        HttpHookHandler handler = new HttpHookHandler();
        Registration registration0 = api.proxy().registerRequestHandler(handler);
        Registration registration1 = api.proxy().registerResponseHandler(handler);
        Registration registration2 = api.http().registerHttpHandler(handler);
        registrations = new Registration[]{registration0, registration1, registration2};
    }

    public void stop() {
        IHttpHooker hooker = HttpHookHandler.hooker;
        hooker.destroy();
        for (Registration registration : registrations) {
            registration.deregister();
        }
        registrations = null;
    }

    public abstract T newHooker();

    public abstract String getInput();

    public abstract void resetInput();
}
