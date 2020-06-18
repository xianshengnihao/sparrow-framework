package com.sina.sparrowframework.tools.utility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * <p></p>
 *
 * @since 2019/7/8 16:47
 */
public abstract class Assert  {

    protected Assert() {

    }

    /**
     * @see String#format(String, Object...)
     */
    public static void notNull(Object object, String format, Object... args) throws IllegalArgumentException {
        if (object == null) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * @see String#format(String, Object...)
     */
    public static void assertNull(Object object, String format, Object... args) throws IllegalArgumentException {
        if (object != null) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * 此方法与 {@link #notNull(Object, String, Object...)} 不同的是此方法抛出的是 {@link IllegalArgumentException}
     *
     * @see String#format(String, Object...)
     */
    public static void assertNotNull(Object object, String format, Object... args) throws IllegalArgumentException {
        if (object == null) {
            throwIllegalArgumentException(format, args);
        }
    }


    public static void isFalse(boolean expression, String format, Object... args) throws IllegalArgumentException {
        if (expression) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * 此方法与 {@link Assert#isFalse(boolean, String, Object...)}  不同的是此方法抛出的是 {@link IllegalArgumentException}
     *
     * @see String#format(String, Object...)
     */
    public static void assertFalse(boolean expression, String format, Object... args) throws IllegalArgumentException {
        if (expression) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void isTrue(boolean expression, String format, Object... args) throws IllegalArgumentException {
        isFalse(!expression, format, args);
    }

    /**
     * 此方法与 {@link Assert#isTrue(boolean, String, Object...)}  不同的是此方法抛出的是 {@link IllegalArgumentException}
     *
     * @see String#format(String, Object...)
     */
    public static void assertTrue(boolean expression, String format, Object... args) throws IllegalArgumentException {
        assertFalse(!expression, format, args);
    }


    /**
     * 若两都 {@code null} 则认为两者不相待
     */
    public static void assertEquals(Object actual, Object expected, String format, Object... args) throws IllegalArgumentException {
        boolean notEquals = actual == null && expected == null
                || expected != null && !expected.equals(actual)
                || !actual.equals(expected);

        if (notEquals) {
            throwIllegalArgumentException(format, args);
        }

    }

    /**
     * 若两都 {@code null} 则认为两者不相待
     */
    public static void assertNotEquals(Object actual, Object expected, String format, Object... args) throws IllegalArgumentException {
        boolean equals = expected != null && expected.equals(actual)
                || actual != null && actual.equals(expected);

        if (equals) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * 断言 number 大于 0
     * <p>
     * 目前仅支持
     * <ul>
     * <li>{@link Integer}</li>
     * <li>{@link Long}</li>
     * <li>{@link BigDecimal}</li>
     * <li>{@link Double}</li>
     * <li>{@link BigInteger}</li>
     * <li>{@link Float}</li>
     * <li>{@link Short}</li>
     * <li>{@link Byte}</li>
     * </ul>
     * </p>
     */
    public static void assertGtZero(Number number, String format, Object... args) throws IllegalArgumentException {
        assertNotNull(number, format, args);

        boolean legal;
        if (number instanceof Integer) {
            legal = ((Integer) number).compareTo(0) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Long) {
            legal = ((Long) number).compareTo(0L) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof BigDecimal) {
            legal = ((BigDecimal) number).compareTo(BigDecimal.ZERO) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Double) {
            legal = ((Double) number).compareTo(0.00) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof BigInteger) {
            legal = ((BigInteger) number).compareTo(BigInteger.ZERO) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Float) {
            legal = ((Float) number).compareTo(0.0F) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Short) {
            legal = ((Short) number).compareTo((short) 0) > 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Byte) {
            legal = ((Byte) number).compareTo((byte) 0) > 0;
            Assert.assertTrue(legal, format, args);
        } else {
            throw new IllegalArgumentException(String.format("type[%s] not com.sina.sparrowframework.rockemq.support", number.getClass().getName()));
        }
    }


    /**
     * 断言 number 大于等于 0
     * <p>
     * 目前仅支持
     * <ul>
     * <li>{@link Integer}</li>
     * <li>{@link Long}</li>
     * <li>{@link BigDecimal}</li>
     * <li>{@link Double}</li>
     * <li>{@link BigInteger}</li>
     * <li>{@link Float}</li>
     * <li>{@link Short}</li>
     * <li>{@link Byte}</li>
     * </ul>
     * </p>
     */
    public static void assertGeZero(Number number, String format, Object... args) throws IllegalArgumentException {
        assertNotNull(number, format, args);

        boolean legal;
        if (number instanceof Integer) {
            legal = ((Integer) number).compareTo(0) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Long) {
            legal = ((Long) number).compareTo(0L) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof BigDecimal) {
            legal = ((BigDecimal) number).compareTo(BigDecimal.ZERO) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Double) {
            legal = ((Double) number).compareTo(0.00) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof BigInteger) {
            legal = ((BigInteger) number).compareTo(BigInteger.ZERO) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Float) {
            legal = ((Float) number).compareTo(0.0F) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Short) {
            legal = ((Short) number).compareTo((short) 0) >= 0;
            Assert.assertTrue(legal, format, args);
        } else if (number instanceof Byte) {
            legal = ((Byte) number).compareTo((byte) 0) >= 0;
            Assert.assertTrue(legal, format, args);
        } else {
            throw new IllegalArgumentException(String.format("type[%s] not com.sina.sparrowframework.rockemq.support", number.getClass().getName()));
        }
    }

    /**
     * 当 number 不为 null 时 断言 number 大于 0
     */
    public static void assertOptionalGt(Number number, String format, Object... args) throws IllegalArgumentException {
        if (number != null) {
            assertGtZero(number, format, args);
        }
    }

    /**
     * 当 number 不为 null 时 断言 number 大于等于 0
     */
    public static void assertOptionalGeZero(Number number, String format, Object... args) throws IllegalArgumentException {
        if (number != null) {
            assertGeZero(number, format, args);
        }
    }




    public static void assertGt(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) <= 0) {
            throwIllegalArgumentException(format, args);
        }
    }


    public static void assertGe(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) < 0) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertLt(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) >= 0) {
            throwIllegalArgumentException(format, args);
        }
    }


    public static void assertLe(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) > 0) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertEq(BigDecimal number1, BigDecimal number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) != 0) {
            throwIllegalArgumentException(format, args);
        }
    }


    public static void assertGt(Integer number1, Integer number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) <= 0) {
            throwIllegalArgumentException(format, args);
        }
    }


    public static void assertGe(Integer number1, Integer number2, String format, Object... args)
            throws IllegalArgumentException {
        if (number1.compareTo(number2) < 0) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * @throws IllegalArgumentException 1.date 为 null;2.date 不是未来时间;
     */
    public static void assertFuture(LocalDateTime dateTime, String format, Object... args) throws IllegalArgumentException {
        if (!dateTime.isAfter(LocalDateTime.now())) {
            throwIllegalArgumentException(format, args);
        }
    }


    /**
     * @throws IllegalArgumentException 1.date 为 null;2.date 不是过去时间;
     */
    public static void assertPast(LocalDateTime dateTime, String format, Object... args) throws IllegalArgumentException {
        if (!dateTime.isBefore(LocalDateTime.now())) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * @throws IllegalArgumentException 1.date 为 null;2.date 不是未来时间;
     */
    public static void assertFuture(LocalDate date, String format, Object... args) throws IllegalArgumentException {
        if (!date.isAfter(LocalDate.now())) {
            throwIllegalArgumentException(format, args);
        }
    }


    /**
     * @throws IllegalArgumentException 1.date 为 null;2.date 不是过去时间;
     */
    public static void assertPast(LocalDate date, String format, Object... args) throws IllegalArgumentException {
        if (!date.isBefore(LocalDate.now())) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertNotEmpty(Collection<?> collection, String format, Object... args) throws IllegalArgumentException {
        if (null == collection || collection.isEmpty()) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertNotEmpty(Map<?, ?> map, String format, Object... args) throws IllegalArgumentException {
        if (map.isEmpty() || null == map) {
            throwIllegalArgumentException(format, args);
        }
    }

    /**
     * Assert that the given String contains valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @see StrToolkit#hasText
     * @throws IllegalArgumentException if the text does not contain valid text content
     */
    public static void hasText(String text, String message) {
        if (!StrToolkit.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }


    private static void throwIllegalArgumentException(String format, Object... args) {
        String text = args == null ? format : String.format(format, args);
        throw new IllegalArgumentException(text);
    }

}
