package com.sina.sparrowframework.tools.utility;

import java.util.UUID;

public class UUIDUtils {

    private UUIDUtils() {
    }

    /**
     * @return 带有 {@code -} 的36位UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     *
     * @return 不带 {@code -} 的32位UUID
     */
    public static String randomUUIDWithNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
