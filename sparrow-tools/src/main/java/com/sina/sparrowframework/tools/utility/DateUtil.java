package com.sina.sparrowframework.tools.utility;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author tianye6
 * @date 2019/5/10 10:25
 */
public abstract class DateUtil {

    public static final String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "uuuu-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String MINUTE_FORMAT = "HH:mm";
    public static final String NO_PARTITION_DATE_FORMAT = "uuuuMMdd";
    public static final String HH_FORMAT = "uuuuMMddHH";
    public static final LocalDate DEFAULT_MIN_DATE = LocalDate.of(1970, 1, 1);
    public static final LocalDate DEFAULT_MAX_DATE = LocalDate.of(9999, 12, 31);
    public static final String DATETIME_FORMAT = "uuuuMMddHHmmss";
    public static final String CHINA_ZONE = "+08:00";
    public static final ZoneId ZONE8 = ZoneId.of("+08:00");
    public static final ZoneId GMT = ZoneId.of("GMT");
    public static final TimeZone TIME_ZONE8;
    public static final ZoneOffset ZONE_OFFSET8;
    public static final String MONTH_DAY_FORMAT = "MM-dd";
    public static final String CLOSE_DATE_FORMAT = "uuuuMMdd";
    public static final String CLOSE_DATE_TIME_FORMAT = "uuuuMMddHHmmss";
    public static final String FULL_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";
    public static final String ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ssZ";
    public static final String FULL_ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSZ";
    public static final String LOCALE_ZONE_DATE_TIME_FORMAT = "E MMM dd HH:mm:ss Z uuuu";
    public static final String MINUTE_DATE_TIME_SPOT = "uuuu.MM.dd HH:mm";
    public static final String SECOND_DATE_TIME_SPOT = "uuuu.MM.dd HH:mm:ss";
    public static final String DATE_TIME_SPOT = "uuuu.MM.dd";
    public static final String MONTH_DATE_TIME_SPOT = "MM.dd";
    public static final String UUUU_MM_DD_HH_MM_SS = "uuuu-MM-dd HH:mm:ss SSS";
    public static final String DATE_TIME_YMD = "uuuu年MM月dd日 HH:mm:ss";
    public static final String DATE_YMD = "uuuu年MM月dd日";
    public static final String HH_MM_SS_SSS = "HHmmssSSS";
    public static final String HH_MM_SS = "HHmmss";

    public static final DateTimeFormatter DATE_FORMATTER;
    public static final DateTimeFormatter MONTH_DAY_FORMATTER;
    public static final DateTimeFormatter TIME_FORMATTER;
    public static final DateTimeFormatter MINUTE_FORMATTER;
    public static final DateTimeFormatter DATETIME_FORMATTER;
    public static final DateTimeFormatter CLOSE_DATE_FORMATTER;
    public static final DateTimeFormatter CLOSE_DATE_TIME_FORMATTER;
    public static final DateTimeFormatter FULL_DATE_TIME_FORMATTER;
    public static final DateTimeFormatter ZONE_DATE_TIME_FORMATTER;
    public static final DateTimeFormatter FULL_ZONE_DATE_TIME_FORMATTER;
    public static final DateTimeFormatter ENGLISH_ZONE_FORMATTER;
    public static final DateTimeFormatter MINUTE_DATE_TIME_SPOT_FORMATTER;
    public static final DateTimeFormatter SECOND_DATE_TIME_SPOT_FORMATTER;
    public static final DateTimeFormatter MONTH_DATE_TIME_SPOT_FORMATTER;
    public static final DateTimeFormatter DATE_TIME_SPOT_FORMATTER;
    public static final DateTimeFormatter DATE_TIME_YMD_FORMATTER;
    public static final DateTimeFormatter DATE_YMD_FORMATTER;

    /**
     * 1970-01-01 08:00:00 即东八区对应的时间
     */
    public static final LocalDateTime SOURCE_DATE_TIME = LocalDateTime.ofInstant(Instant.EPOCH, ZONE8);
    public static final LocalDate SOURCE_DATE = SOURCE_DATE_TIME.toLocalDate();


    static {
        TIME_ZONE8 = TimeZone.getTimeZone(ZONE8);
        ZONE_OFFSET8 = ZoneOffset.of("+08:00");
        DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
        MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
        TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
        MINUTE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        CLOSE_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");
        CLOSE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
        FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");
        ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ssZ");
        FULL_ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSSZ");
        ENGLISH_ZONE_FORMATTER = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss Z uuuu", Locale.ENGLISH);
        MINUTE_DATE_TIME_SPOT_FORMATTER = DateTimeFormatter.ofPattern(MINUTE_DATE_TIME_SPOT);
        SECOND_DATE_TIME_SPOT_FORMATTER = DateTimeFormatter.ofPattern(SECOND_DATE_TIME_SPOT);
        DATE_TIME_SPOT_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_SPOT);
        MONTH_DATE_TIME_SPOT_FORMATTER = DateTimeFormatter.ofPattern(MONTH_DATE_TIME_SPOT);
        DATE_TIME_YMD_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_YMD);
        DATE_YMD_FORMATTER = DateTimeFormatter.ofPattern(DATE_YMD);

    }


    public static ChronoUnit convertTimeUnit(TimeUnit timeUnit) {
        ChronoUnit unit;
        switch (timeUnit) {
            case SECONDS:
                unit = ChronoUnit.SECONDS;
                break;
            case MINUTES:
                unit = ChronoUnit.MINUTES;
                break;
            case HOURS:
                unit = ChronoUnit.HOURS;
                break;
            case DAYS:
                unit = ChronoUnit.DAYS;
                break;
            case MICROSECONDS:
                unit = ChronoUnit.MICROS;
                break;
            case NANOSECONDS:
                unit = ChronoUnit.NANOS;
                break;
            case MILLISECONDS:
                unit = ChronoUnit.MILLIS;
                break;
            default:
                throw new IllegalArgumentException();

        }
        return unit;
    }


    public static LocalDateTime string2LocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static Date localDateTODate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static Date localDateTimeTODate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static LocalDate dateTOLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    public static String HHMM_FormatString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(HH_FORMAT));
    }

    public static String fileDateStr(LocalDate billDate) {
        StringBuffer localDatestr = new StringBuffer();
        String datestr = billDate.plusDays(1L).format(DateTimeFormatter.ofPattern(NO_PARTITION_DATE_FORMAT));
        int hour = LocalDateTime.now().getHour();
        if (hour < 10) {
            localDatestr.append(datestr).append(0).append(hour);
        } else {
            localDatestr.append(datestr).append(hour);
        }
        return localDatestr.toString();
    }

    public static String localDateTimeTOString(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime required");
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static String localDateTime2String(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime required");
        return localDateTime.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    /**
     * @return true 表示 date 等于 {@link #SOURCE_DATE}
     */
    public static boolean isSource(LocalDate date) {
        return SOURCE_DATE.equals(date);
    }

    /**
     * 比较 dateTime 是否等于 东八区 1970-01-01 08:00:00 ,这个过程将会忽略毫秒.
     * 如果 需要精确到毫秒请使用 {@link LocalDateTime#equals(Object)}
     *
     * @return true 表示 dateTime 是 东八区 1970-01-01 08:00:00
     */
    public static boolean isSource(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime required");
        }
        boolean yes;
        yes = SOURCE_DATE_TIME.toLocalDate().equals(dateTime.toLocalDate());
        if (yes) {
            yes = SOURCE_DATE_TIME.getHour() == dateTime.getHour()
                    && SOURCE_DATE_TIME.getMinute() == dateTime.getMinute()
                    && SOURCE_DATE_TIME.getSecond() == dateTime.getSecond();
        }
        return yes;
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static LocalDateTime toDateTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), DateUtil.ZONE8);
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static ZonedDateTime toZoneDateTime(long millis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), DateUtil.ZONE8);
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static LocalDate toDate(long millis) {
        return toDateTime(millis).toLocalDate();
    }

    /**
     * @param millis 毫秒
     * @see System#currentTimeMillis()
     */
    public static LocalTime toTime(long millis) {
        return toDateTime(millis).toLocalTime();
    }

    public static LocalDate toMinDate(YearMonth yearMonth) {
        return LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
    }

    public static LocalDate toMaxDate(YearMonth yearMonth) {
        LocalDate minDate = toMinDate(yearMonth);
        return minDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDateTime toMinDateTime(YearMonth yearMonth) {
        return LocalDateTime.of(toMinDate(yearMonth), LocalTime.MIDNIGHT);
    }

    public static LocalDateTime toMaxDateTime(YearMonth yearMonth) {
        return LocalDateTime.of(toMaxDate(yearMonth), LocalTime.MAX);
    }

    public static LocalDate toMinDate(Year year) {
        return LocalDate.of(year.getValue(), Month.JANUARY, 1);
    }

    public static LocalDate toMaxDate(Year year) {
        return LocalDate.of(year.getValue(), Month.DECEMBER, 31);
    }

    public static LocalDateTime toMinWeekDateTime(final LocalDate date) {
        return LocalDateTime.of(date.minusDays(date.getDayOfWeek().ordinal()), LocalTime.MIDNIGHT);
    }


    public static LocalDateTime toMaxWeekDateTime(LocalDate date) {
        int dayCount = 6 - date.getDayOfWeek().ordinal();
        return LocalDateTime.of(date.plusDays(dayCount), LocalTime.MAX);
    }

    public static LocalDateTime toQuarterMinDateTime(YearMonth month) {
        YearMonth first = YearMonth.of(month.getYear(), month.getMonth().firstMonthOfQuarter());
        return toMinDateTime(first);
    }

    public static LocalDateTime toQuarterMaxDateTime(YearMonth month) {
        YearMonth last = YearMonth.of(month.getYear(), month.getMonth().firstMonthOfQuarter().plus(2));
        return toMaxDateTime(last);
    }

    public static LocalDateTime toMinDateTime(Year year) {
        return LocalDateTime.of(toMinDate(year), LocalTime.MIDNIGHT);
    }

    public static LocalDateTime toMaxDateTime(Year year) {
        return LocalDateTime.of(toMaxDate(year), LocalTime.MAX);
    }


    public static boolean isLastOfQuarter(Month month) {
        return month == Month.MAY
                || month == Month.JUNE
                || month == Month.SEPTEMBER
                || month == Month.DECEMBER;

    }

    /***
     * @Author tianye6
     * @Description 获得当天剩余秒
     * @Date 22:36 2018/6/5
     * @Param []
     * @return java.com.sina.sparrowframework.tools.lang.Long
     **/
    public static Long getTodayLeftSeconds() {
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
    }

    public static String formatCurrentDateSSS() {
        SimpleDateFormat format = new SimpleDateFormat(UUUU_MM_DD_HH_MM_SS);
        return format.format(new Date());
    }

    /**
     * 获取毫秒
     *
     * @param localDate
     * @return
     */
    public static long getMillis(LocalDate localDate) {
        return localDate.atStartOfDay(DateUtil.ZONE8).toInstant().toEpochMilli();
    }

    /**
     * 获取YYYYMMDD LocalDate
     *
     * @param localDate
     * @return
     */
    public static String getYYYYMMDD(LocalDate localDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(NO_PARTITION_DATE_FORMAT);
        return localDate.format(fmt);
    }

    /**
     * 获取 LocalDateTime
     *
     * @param localDateTime
     * @return
     */
    public static String getYYYYMMDD(LocalDateTime localDateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(NO_PARTITION_DATE_FORMAT);
        return localDateTime.format(fmt);
    }

    /**
     * 获取 HHMMSSNNN LocalDateTime
     *
     * @param localDateTime
     * @return
     */
    public static String getHHMMSSNNN(LocalDateTime localDateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(HH_MM_SS_SSS);
        return localDateTime.format(fmt);
    }


    /**
     * UUUUMMDDHHMMSS 转换LocalDateTime
     *
     * @return
     */
    public static LocalDateTime parseUUUUMMDDHHMMSS(String str) {
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(DateUtil.CLOSE_DATE_TIME_FORMAT));
    }

    /**
     * UUUUMMDD 转换LocalDateTime
     *
     * @return
     */
    public static LocalDateTime parseUUUUMMDD(String str) {
        LocalDate localDate = LocalDate.parse(str, DateTimeFormatter.ofPattern(DateUtil.CLOSE_DATE_FORMAT));
        return parseLocalDate(localDate);
    }

    /**
     * UUUUMMDD 转换LocalDate
     *
     * @return
     */
    public static LocalDate parseUUUUMMDDToLocalDate(String str) {
        LocalDate localDate = LocalDate.parse(str, DateTimeFormatter.ofPattern(DateUtil.CLOSE_DATE_FORMAT));
        return localDate;
    }


    /**
     * LocalDate 转换LocalDateTime
     *
     * @return
     */
    public static LocalDateTime parseLocalDate(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.parse("00:00:00"));
    }

    /**
     * 天数转换为以年月日表示的期限
     * 默认年：360，月：30
     * @param dayNumber
     * @return
     */
    public static String transferDayNumber(Integer dayNumber) {
        return transferDayNumber(dayNumber,360,30);
    }
    /**
     * 把天数转换为以年月日表示的期限
     *
     * @param dayNumber   需要转换的天数
     * @param yearNumber  一年按照多少天来进行转换不能为0
     * @param monthNumber 一个月按照多少天来转换不能为0
     * @return 转换完格式为4年3月12天
     */
    public static String transferDayNumber(Integer dayNumber,Integer yearNumber,Integer monthNumber) {
        String result = "";

        //转换天数，yearNumber与monthNumber不能为0
        if(yearNumber<=0||monthNumber<=0){
            throw new DateTimeException("转换天数，yearNumber与monthNumber不能为0 yearNumber:"+yearNumber+"monthNumber:"+monthNumber);
        }
        int year = dayNumber / yearNumber;
        if (year != 0) {
            result += year + "年";
        }
        int yearRemainingDay = dayNumber % yearNumber;
        int month = yearRemainingDay / monthNumber;
        if (month != 0) {
            result += month + "月";
        }
        int monthRemainingDay = yearRemainingDay % monthNumber;
        if (monthRemainingDay != 0) {
            result += monthRemainingDay + "天";
        }

        if (result.equals("")) {
            result = "0天";
        }

        return result;
    }


    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    protected static int age(long birthday, long dateToCompare) {
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.setTimeInMillis(birthday);
        int age = year - cal.get(Calendar.YEAR);

        final int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {

            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            final boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if ((false == isLastDayOfMonth || false == isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth) {
                // 如果生日在当月，但是未达到生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }


    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static Long timeToLong(String time, String expression) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(expression);
        LocalDateTime parse = LocalDateTime.parse("2018-05-29 13:52:50", ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
