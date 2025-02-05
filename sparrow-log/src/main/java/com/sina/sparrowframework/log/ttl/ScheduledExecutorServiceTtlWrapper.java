package com.sina.sparrowframework.log.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.spi.TtlEnhanced;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@link TransmittableThreadLocal} Wrapper of {@link ScheduledExecutorService},
 * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable} or {@link Callable}
 * to the execution time of {@link Runnable} or {@link Callable}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 0.9.0
 */
class ScheduledExecutorServiceTtlWrapper extends ExecutorServiceTtlWrapper implements ScheduledExecutorService, TtlEnhanced {
    final ScheduledExecutorService scheduledExecutorService;

    public ScheduledExecutorServiceTtlWrapper(@NonNull ScheduledExecutorService scheduledExecutorService, boolean idempotent) {
        super(scheduledExecutorService, idempotent);
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @NonNull
    @Override
    public ScheduledFuture<?> schedule(@NonNull Runnable command, long delay, @NonNull TimeUnit unit) {
        return scheduledExecutorService.schedule(TtlRunnable.get(command, false, idempotent), delay, unit);
    }

    @NonNull
    @Override
    public <V> ScheduledFuture<V> schedule(@NonNull Callable<V> callable, long delay, @NonNull TimeUnit unit) {
        return scheduledExecutorService.schedule(TtlCallable.get(callable, false, idempotent), delay, unit);
    }

    @NonNull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@NonNull Runnable command, long initialDelay, long period, @NonNull TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(TtlRunnable.get(command, false, idempotent), initialDelay, period, unit);
    }

    @NonNull
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@NonNull Runnable command, long initialDelay, long delay, @NonNull TimeUnit unit) {
        return scheduledExecutorService.scheduleWithFixedDelay(TtlRunnable.get(command, false, idempotent), initialDelay, delay, unit);
    }

    @Override
    @NonNull
    public ScheduledExecutorService unwrap() {
        return scheduledExecutorService;
    }
}
