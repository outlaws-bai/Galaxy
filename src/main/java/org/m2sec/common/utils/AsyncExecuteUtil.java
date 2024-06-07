package org.m2sec.common.utils;

import org.m2sec.GalaxyMain;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class AsyncExecuteUtil {

    private static boolean executing = false; // 标志当前是否有任务在执行

    public static synchronized void execute(
            List<Runnable> workRunnables, Runnable afterCompleteRunnable) {
        if (executing) {
            throw new IllegalStateException("workExecutor being executed. please wait");
        }

        // 设置标志位为 true，表示正在执行 executeMethods 方法
        executing = true;

        // 创建计数器，用于等待所有workRunnables执行完成
        CountDownLatch latch = new CountDownLatch(workRunnables.size());

        // 提交workRunnables任务到线程池
        for (Runnable workRunnable : workRunnables) {
            GalaxyMain.workExecutor.execute(
                    () -> {
                        try {
                            // 执行workRunnable
                            workRunnable.run();
                        } finally {
                            // workRunnable执行完成后，计数器减一
                            latch.countDown();
                        }
                    });
        }

        // 创建管理线程，等待workRunnables执行完成后执行afterCompleteRunnable
        new Thread(
                        () -> {
                            try {
                                // 等待所有workRunnables执行完成
                                latch.await();
                                // 执行afterCompleteRunnable
                                afterCompleteRunnable.run();
                            } catch (InterruptedException e) {
                                //
                            } finally {
                                // 设置标志位为 false，表示 executeMethods 方法执行完成
                                executing = false;
                            }
                        })
                .start();
    }
}
