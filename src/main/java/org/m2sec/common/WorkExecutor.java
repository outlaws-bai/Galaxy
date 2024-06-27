package org.m2sec.common;

import org.m2sec.common.utils.CompatUtil;

import javax.annotation.Nullable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: outlaws-bai
 * @date: 2024/6/26 22:20
 * @description:
 */

public class WorkExecutor extends ThreadPoolExecutor {

    public static final WorkExecutor INSTANCE = new WorkExecutor();

    public WorkExecutor() {
        this(0, CompatUtil.getCPUCount() * 2 - 1,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
    }

    public WorkExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                        BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void batchExecute(Runnable... runnables) {
        for (Runnable runnable : runnables) {
            this.execute(runnable);
        }
    }

    public void beforeBatchExecute(Runnable beforeRunnable, Runnable... runnables) {
        beyondBatchExecute(beforeRunnable, null, runnables);
    }

    public void afterBatchExecute(Runnable afterRunnable, Runnable... runnables) {
        beyondBatchExecute(null, afterRunnable, runnables);
    }

    public void beyondBatchExecute(@Nullable Runnable beforeRunnable, @Nullable Runnable afterRunnable,
                                   Runnable... runnables) {
        if (beforeRunnable != null) this.execute(beforeRunnable);
        if (runnables.length == 0) {
            if (afterRunnable != null) {
                this.execute(afterRunnable);
            }
            return;
        }

        // 使用 AtomicInteger 跟踪 runnable2 任务的完成数量
        AtomicInteger count = new AtomicInteger(runnables.length);
        for (Runnable r : runnables) {
            this.execute(() -> {
                try {
                    r.run();
                } finally {
                    // 当每个 runnable2 任务完成时，计数器减一
                    if (count.decrementAndGet() == 0) {
                        // 当所有 runnable2 任务完成后，执行 beforeRunnable
                        if (afterRunnable != null) {
                            this.execute(afterRunnable);
                        }
                    }
                }
            });
        }
    }
}
