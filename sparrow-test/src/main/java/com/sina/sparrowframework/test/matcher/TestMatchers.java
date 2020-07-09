//package com.sina.sparrowframework.test.matcher;
//
//import org.hamcrest.CoreMatchers;
//import org.hamcrest.CustomTypeSafeMatcher;
//import org.springframework.util.StringUtils;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//
///**
// * created  on 2018/5/22.
// */
//public abstract class TestMatchers extends CoreMatchers{
//
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern( "uuuu-MM-dd" );
//
//    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss" );
//
//    private static final DateTimeFormatter DATE_TIME_NO_SECOND_FORMATTER = DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm" );
//
//    public static final CustomTypeSafeMatcher<String> HAS_TEXT = new CustomTypeSafeMatcher<String>("a text") {
//        @Override
//        protected boolean matchesSafely(String item) {
//            return StringUtils.hasText( item );
//        }
//    };
//
//    public static final CustomTypeSafeMatcher<String> BIG_DECIMAL = new CustomTypeSafeMatcher<String>("a decimal ,scale is 2 or 6") {
//        @Override
//        protected boolean matchesSafely(String item) {
//            boolean legal;
//            try {
//                BigDecimal amount =   new BigDecimal( item );
//                legal = amount.scale() == 2 || amount.scale() == 6;
//            } catch (Exception e) {
//                legal = false;
//            }
//            return legal;
//        }
//    };
//
//    public static final CustomTypeSafeMatcher<String> LONG = new CustomTypeSafeMatcher<String>("a Long") {
//        @Override
//        protected boolean matchesSafely(String item) {
//            boolean legal;
//            try {
//                Long.parseLong( item );
//                legal = true;
//            } catch (NumberFormatException e) {
//                legal = false;
//            }
//            return legal;
//        }
//    };
//
//    public static final CustomTypeSafeMatcher<Integer> INTEGER = new CustomTypeSafeMatcher<Integer>("a Integer") {
//        @Override
//        protected boolean matchesSafely(Integer item) {
//            return true;
//        }
//    };
//
//    public static final CustomTypeSafeMatcher<String> DATE = new CustomTypeSafeMatcher<String>("a LocalDate") {
//        @Override
//        protected boolean matchesSafely(String item) {
//            boolean legal;
//            try {
//                LocalDate.parse(item,DATE_FORMATTER);
//                legal = true;
//            } catch (DateTimeParseException e) {
//                legal = false;
//            }
//            return legal;
//        }
//    };
//
//    public static final CustomTypeSafeMatcher<String> DATE_TIME = new CustomTypeSafeMatcher<String>("a LocalDateTime") {
//        @Override
//        protected boolean matchesSafely(String item) {
//            boolean legal;
//            try {
//                LocalDateTime.parse(item,DATE_TIME_FORMATTER);
//                legal = true;
//            } catch (DateTimeParseException e) {
//                legal = false;
//            }
//            return legal;
//        }
//    };
//
//    public static final CustomTypeSafeMatcher<String> DATE_TIME_NO_SECOND = new CustomTypeSafeMatcher<String>("a LocalDateTime") {
//        @Override
//        protected boolean matchesSafely(String item) {
//            boolean legal;
//            try {
//                LocalDateTime.parse(item,DATE_TIME_NO_SECOND_FORMATTER);
//                legal = true;
//            } catch (DateTimeParseException e) {
//                legal = false;
//            }
//            return legal;
//        }
//    };
//
//    public static org.hamcrest.Matcher<String> hasText(){
//        return HAS_TEXT;
//    }
//
//    public static org.hamcrest.Matcher<String> isBigDecimal(){
//        return BIG_DECIMAL;
//    }
//
//    public static org.hamcrest.Matcher<String> isLong(){
//        return LONG;
//    }
//
//    public static org.hamcrest.Matcher<Integer> isInteger(){
//        return INTEGER;
//    }
//
//    public static org.hamcrest.Matcher<String> isLocalDate(){
//        return DATE;
//    }
//
//    public static org.hamcrest.Matcher<String> isLocalDateTime(){
//        return DATE_TIME;
//    }
//
//
//
//    public static org.hamcrest.Matcher<Iterable<Object>> everyItemNotNull(){
//        return CoreMatchers.everyItem(CoreMatchers.notNullValue()  );
//    }
//
//    public static <T> org.hamcrest.Matcher<Iterable<T>> everyItemNotNull(Class<T> type){
//        return CoreMatchers.everyItem(CoreMatchers.notNullValue(type)  );
//    }
//
//    public static org.hamcrest.Matcher<Iterable<String>> everyItemHasText(){
//        return CoreMatchers.everyItem(hasText());
//    }
//
//    public static org.hamcrest.Matcher<Iterable<String>> everyItemBigDecimal(){
//        return CoreMatchers.everyItem(BIG_DECIMAL );
//    }
//
//    public static org.hamcrest.Matcher<Iterable<String>> everyItemLocalDate(){
//        return CoreMatchers.everyItem(DATE );
//    }
//
//    public static org.hamcrest.Matcher<Iterable<String>> everyItemLocalDateTime(){
//        return CoreMatchers.everyItem(DATE_TIME );
//    }
//
//    public static org.hamcrest.Matcher<Iterable<String>> everyItemLocalDateTimeNoSecond(){
//        return CoreMatchers.everyItem(DATE_TIME_NO_SECOND );
//    }
//
//    public static org.hamcrest.Matcher<Iterable<String>> everyItemLong(){
//        return CoreMatchers.everyItem(LONG );
//    }
//
//    public static org.hamcrest.Matcher<Iterable<Integer>> everyItemInteger(){
//        return CoreMatchers.everyItem(INTEGER );
//    }
//
//    public static <E> org.hamcrest.Matcher<java.util.Collection<? extends E>> notEmpty() {
//        return not( org.hamcrest.collection.IsEmptyCollection.empty() );
//    }
//
//
//
//
//
//}
