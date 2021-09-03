package com.sina.sparrowframework.log.ttl;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.alibaba.ttl.spi.TtlWrapper;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author wxn
 * @date 2021/9/3 11:57 上午
 */
public class ThreadPoolExecutorServiceTtlWrapper extends ThreadPoolExecutor implements  ExecutorService
        , Executor, TtlWrapper<Executor>, TtlEnhanced {

    private ThreadPoolExecutor threadPoolExecutor;
    protected final boolean idempotent;

    public ThreadPoolExecutorServiceTtlWrapper (ThreadPoolExecutor threadPoolExecutor, boolean idempotent){
        super(threadPoolExecutor.getCorePoolSize()
                , threadPoolExecutor.getMaximumPoolSize()
                , threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS)
                , TimeUnit.SECONDS, threadPoolExecutor.getQueue());
        this.threadPoolExecutor = threadPoolExecutor;
        this.idempotent = idempotent;
    }

    @Override
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        return threadPoolExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return threadPoolExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        return threadPoolExecutor.awaitTermination(timeout, unit);
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return threadPoolExecutor.submit(TtlCallable.get(task, false, idempotent));
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Runnable task, T result) {
        return threadPoolExecutor.submit(TtlRunnable.get(task, false, idempotent), result);
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull Runnable task) {
        return threadPoolExecutor.submit(TtlRunnable.get(task, false, idempotent));
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return threadPoolExecutor.invokeAll(TtlCallable.gets(tasks, false, idempotent));
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        return threadPoolExecutor.invokeAll(TtlCallable.gets(tasks, false, idempotent), timeout, unit);
    }

    @NonNull
    @Override
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return threadPoolExecutor.invokeAny(TtlCallable.gets(tasks, false, idempotent));
    }

    @Override
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return threadPoolExecutor.invokeAny(TtlCallable.gets(tasks, false, idempotent), timeout, unit);
    }


    @Override
    public void execute(@NonNull Runnable command) {
        threadPoolExecutor.execute(TtlRunnable.get(command, false, idempotent));
    }

    @Override
    @NonNull
    public Executor unwrap() {
        return threadPoolExecutor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        ThreadPoolExecutorServiceTtlWrapper that = (ThreadPoolExecutorServiceTtlWrapper) o;

        return threadPoolExecutor.equals(that.threadPoolExecutor);
    }

    @Override
    public int hashCode() {
        return threadPoolExecutor.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + threadPoolExecutor.toString();
    }
}
