package com.sina.sparrowframework.tools.utility;

import java.time.*;
import java.util.*;

/**
 * created  on 31/03/2018.
 */
public abstract class ArrayUtils {

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];

    public static final Long[] EMPTY_LONG_ARRAY = new Long[0];

    public static final int[] EMPTY_INT_ARRAY = new int[0];

    public static LocalTime[] EMPTY_TIME = new LocalTime[0];

    public static LocalDate[] EMPTY_DATE = new LocalDate[0];

    public static YearMonth[] EMPTY_YEAR_MONTH = new YearMonth[0];

    public static MonthDay[] EMPTY_MONTH_DAY = new MonthDay[0];

    public static LocalDateTime[] EMPTY_DATE_TIME = new LocalDateTime[0];

    public static ZonedDateTime[] EMPTY_ZONE_DATE_TIME = new ZonedDateTime[0];


    public static <T> Set<T> asSet(T... e) {
        Set<T> set = new HashSet<>();
        Collections.addAll( set, e );
        return set;
    }

    public static <T> Set<T> asUnmodifiableSet(T... e) {
        return Collections.unmodifiableSet( asSet( e ) );
    }

    public static <T> List<T> asUnmodifiableList(T... e) {
        return Collections.unmodifiableList( Arrays.asList( e ) );
    }


}
