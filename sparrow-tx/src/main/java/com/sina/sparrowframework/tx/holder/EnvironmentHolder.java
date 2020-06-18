package com.sina.sparrowframework.tx.holder;

import org.springframework.core.env.Environment;

public abstract class EnvironmentHolder {

    private static Environment environment;

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        EnvironmentHolder.environment = environment;
    }
}
