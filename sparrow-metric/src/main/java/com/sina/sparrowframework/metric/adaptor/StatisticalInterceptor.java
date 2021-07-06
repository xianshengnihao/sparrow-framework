package com.sina.sparrowframework.metric.adaptor;

import io.prometheus.client.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatisticalInterceptor extends HandlerInterceptorAdapter implements EnvironmentAware {

    public Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Environment env;

    private final NamedThreadLocal<Long> startThreadLocal = new NamedThreadLocal<>("统计耗时开始时间");

    private static final Summary SUMMARY_LATENCY_SECONDS = Summary.build()
            .namespace("reward_app")
            .subsystem("com/sina/sparrowframework/metric/controller")
            .name("summary_latency_seconds")
            .labelNames("bean", "method", "url", "status", "exception")
            .help("Summary of controller handle latency in seconds")
            .register();

    private static final String HOLDER_REQUEST_ATTR = StatisticalInterceptor.class.getName() + ".HOLDER_REQUEST_ATTR";

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        env = environment;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
            throws Exception {
        Long startMilli = System.currentTimeMillis();
        startThreadLocal.set(startMilli);

        if (request.getAttribute(HOLDER_REQUEST_ATTR) != null) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Holder holder = new Holder(handlerMethod.getBeanType().getName(), handlerMethod.getMethod().getName(), System.currentTimeMillis());
        request.setAttribute(HOLDER_REQUEST_ATTR, holder);
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                                @Nullable Exception ex) throws Exception {
        Long startMilli = startThreadLocal.get();
        Long endMilli = System.currentTimeMillis();
        LOG.info("全局接口耗时统计,url:{},共计耗时:{} ms", request.getRequestURI(), endMilli - startMilli);

        Holder holder = (Holder) request.getAttribute(HOLDER_REQUEST_ATTR);
        if (holder != null) {
            SUMMARY_LATENCY_SECONDS.labels(holder.bean, holder.method, request.getRequestURI(), "0", ex == null ? "none" : ex.getClass().getName())
                    .observe((System.currentTimeMillis() - holder.beginTime) / 1000D);
        }
    }

    private static class Holder {
        final String bean;
        final String method;
        final long beginTime;
        Holder(String bean, String method, long beginTime) {
            this.bean = bean;
            this.method = method;
            this.beginTime = beginTime;
        }
    }
}
