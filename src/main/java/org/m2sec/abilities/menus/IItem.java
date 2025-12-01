package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.SwingTools;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public abstract class IItem extends JMenuItem {

    protected final MontoyaApi api;

    protected final Config config;

    public IItem(MontoyaApi api, Config config) {
        this.api = api;
        this.config = config;
        this.setText(displayName());
    }

    public abstract String displayName();

    public abstract boolean isDisplay(ContextMenuEvent event);

    public void safeAction(ContextMenuEvent event) {
        try {
            this.action(event);
        } catch (Exception exc) {
            log.error("action execute error. {} .", exc.getMessage(), exc);
            SwingTools.showErrorStackTraceDialog(api, exc);
        }
    }

    public abstract void action(ContextMenuEvent event);

    /**
     * 在后台线程执行任务，完成后在 EDT 更新 UI
     *
     * @param backgroundTask 后台任务（返回结果）
     * @param uiUpdateTask UI 回调（接收结果）
     * @param <T> 结果类型
     */
    public static <T> void runAsync(Callable<T> backgroundTask, Consumer<T> uiUpdateTask) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return backgroundTask.call(); // 执行后台任务
            }

            @Override
            protected void done() {
                try {
                    uiUpdateTask.accept(get()); // 在 EDT 更新 UI
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute(); // 自动启动
    }
}
