package com.sina.sparrowframework.tools.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 金额计算工具
 * created  on 24/03/2018.
 */
public abstract class AmountUtils {

    /**
     * 中文数字
     */
    public static  final List<String> CHINESE_NUMBERS =
            Collections.unmodifiableList(Arrays.asList("零","壹","贰","叁","肆","伍","陆","柒","捌","玖"));

    /**
     * 中文整数单位
     */
    public static final List<String> CHINESE_INTEGER_UNITS =
            Collections.unmodifiableList(Arrays.asList("整","拾","佰","仟","万","亿"));

    /**
     * 中文小数单位
     */
    public static final List<String> CHINESE_FRACTION_UNITS =
            Collections.unmodifiableList(Arrays.asList("整","角","分"));

    /**
     * 表示金额的 0 和 {@link BigDecimal#ZERO} 不同的是此值带有 2 位小数.
     */
    public static final BigDecimal ZERO = new BigDecimal( "0.00" );
    /**
     * 将 单位元转换为 单位分时使用
     */
    public static final BigDecimal HUNDRED = new BigDecimal( "100.00" );


    /**
     * 汉语中数字大写
     */
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    /**
     * 汉语中货币单位大写，这样的设计类似于占位符
     */
    private static final String[] CN_UPPER_MONETRAY_UNIT = {"分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟"};
    /**
     * 特殊字符：整
     */
    private static final String CN_FULL = "整";
    /**
     * 特殊字符：负
     */
    private static final String CN_NEGATIVE = "负";
    /**
     * 金额的精度，默认值为2
     */
    private static final int MONEY_PRECISION = 2;
    /**
     * 特殊字符：零元整
     */
    private static final String CN_ZEOR_FULL = "零元" + CN_FULL;


    /**
     * 和 {@link #ZERO} 比较
     */
    public static boolean eqZero(BigDecimal amount) {
        return amount != null && amount.compareTo( ZERO ) == 0;
    }

    /**
     * @return true 小于 {@link #ZERO}
     */
    public static boolean ltZero(BigDecimal amount) {
        return amount.compareTo( ZERO ) < 0;
    }

    /**
     * @return true 小于等于 {@link #ZERO}
     */
    public static boolean leZero(BigDecimal amount) {
        return amount.compareTo( ZERO ) <= 0;
    }

    public static boolean glZero(BigDecimal amount) {
        return amount.compareTo( BigDecimal.ZERO ) > 0;
    }

    public static boolean geZero(BigDecimal amount) {
        return amount.compareTo( BigDecimal.ZERO ) >= 0;
    }


    /**
     * 获得以分为单位的金额
     *
     * @param amount 金额,单位元
     * @return 金额以分表示的字符串
     */
    public static String getAmountForCent(BigDecimal amount) {
        // MathContext context = new MathContext( 0, RoundingMode.HALF_UP );
        BigDecimal cent = amount.multiply( HUNDRED ).setScale( 0, RoundingMode.HALF_UP );
        return cent.toPlainString();
    }

    public static BigDecimal getAmountFromCent(String centStr) {
        BigDecimal cent, amount;
        cent = new BigDecimal( centStr );
        amount = cent.divide( HUNDRED, 2, RoundingMode.HALF_EVEN );
        return amount;
    }

    public static BigDecimal setScale(BigDecimal amount) {
        return amount.setScale( 2, BigDecimal.ROUND_HALF_EVEN );
    }

    public static String toChineseNumber(BigDecimal amount){
        return chineseTransformation(amount);
    }



    /**
     * 把输入的金额转换为汉语中人民币的大写
     *
     * @param numberOfMoney 输入的金额
     * @return 对应的汉语大写
     */
    private static String chineseTransformation(BigDecimal numberOfMoney) {
        StringBuffer sb = new StringBuffer();
        // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
        // positive.
        int signum = numberOfMoney.signum();
        // 零元整的情况
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }
        //这里会进行金额的四舍五入
        long number = numberOfMoney.movePointRight(MONEY_PRECISION).setScale(0, 4).abs().longValue();
        // 得到小数点后两位值
        long scale = number % 100;
        int numUnit;
        int numIndex = 0;
        boolean getZero = false;
        // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }
        int zeroSize = 0;
        while (true) {
            if (number <= 0) {
                break;
            }
            // 每次获取到最后一个数
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!(getZero)) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    if (number > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // 让number每次都去掉最后一个数
            number = number / 10;
            ++numIndex;
        }
        // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

}
