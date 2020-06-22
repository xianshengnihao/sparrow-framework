package com.sina.sparrowframework.test;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * created  on 2018/5/21.
 */
public abstract class Assert extends org.testng.Assert {

    public static void assertNotEmpty(Collection collection){
        assertNotEmpty( collection,"" );
    }

    public static void assertNotEmpty(Collection collection,String message){
        if(CollectionUtils.isEmpty( collection )){
            fail(message);
        }
    }

    public static void assertHasText(String text){
        assertHasText( text,"" );
    }
    
    public static void assertHasText(String text,String message){
        if(!StringUtils.hasText( text )){
            fail(message);
        }
    }


    public static void assertGtZero(Number number){
        assertGtZero( number,"" );
    }
    
    public static void assertGtZero(Number number,String message){
        boolean legal;
        if (number instanceof Integer) {
            legal = ((Integer) number).compareTo( 0 ) > 0;
        } else if (number instanceof Long) {
            legal = ((Long) number).compareTo( 0L ) > 0;
        } else if (number instanceof BigDecimal) {
            legal = ((BigDecimal) number).compareTo( BigDecimal.ZERO ) > 0;
        } else if (number instanceof Double) {
            legal = ((Double) number).compareTo( 0.00 ) > 0;
        } else if (number instanceof BigInteger) {
            legal = ((BigInteger) number).compareTo( BigInteger.ZERO ) > 0;
        } else if (number instanceof Float) {
            legal = ((Float) number).compareTo( 0.0F ) > 0;
        } else if (number instanceof Short) {
            legal = ((Short) number).compareTo( (short) 0 ) > 0;
        } else if (number instanceof Byte) {
            legal = ((Byte) number).compareTo( (byte) 0 ) > 0;
        } else {
            throw new IllegalArgumentException( String.format( "type[%s] not support", number.getClass().getName() ) );
        }
       if(!legal){
            fail(message);
       }
    }

    public static void assertGeZero(Number number){
        assertGeZero( number,"" );
    }

    public static void assertGeZero(Number number,String message){
        boolean legal;
        if (number instanceof Integer) {
            legal = ((Integer) number).compareTo( 0 ) >= 0;
        } else if (number instanceof Long) {
            legal = ((Long) number).compareTo( 0L ) >= 0;
        } else if (number instanceof BigDecimal) {
            legal = ((BigDecimal) number).compareTo( BigDecimal.ZERO ) >= 0;
        } else if (number instanceof Double) {
            legal = ((Double) number).compareTo( 0.00 ) > 0;
        } else if (number instanceof BigInteger) {
            legal = ((BigInteger) number).compareTo( BigInteger.ZERO ) >= 0;
        } else if (number instanceof Float) {
            legal = ((Float) number).compareTo( 0.0F ) >= 0;
        } else if (number instanceof Short) {
            legal = ((Short) number).compareTo( (short) 0 ) >= 0;
        } else if (number instanceof Byte) {
            legal = ((Byte) number).compareTo( (byte) 0 ) >= 0;
        } else {
            throw new IllegalArgumentException( String.format( "type[%s] not support", number.getClass().getName() ) );
        }
        if(!legal){
            fail(message);
        }
    }


    public static void assertFuture(LocalDateTime dateTime){
        assertFuture( dateTime,"" );
    }


    public static void assertFuture(LocalDateTime dateTime,String message){
        if(!dateTime.isAfter( LocalDateTime.now() )){
            fail(message);
        }
    }

    public static void assertPast(LocalDateTime dateTime){
        assertPast( dateTime,"" );
    }

    public static void assertPast(LocalDateTime dateTime,String message){
        if(!dateTime.isBefore( LocalDateTime.now() )){
            fail(message);
        }
    }

}
