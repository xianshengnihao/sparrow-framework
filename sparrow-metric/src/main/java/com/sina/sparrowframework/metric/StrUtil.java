package com.sina.sparrowframework.metric;

import org.springframework.util.StringUtils;

/**
 * @author songbo1
 * @date 2021年07月06日
 */
public class StrUtil {

    /**
     * 横线转下划线
     * @return
     */
    public static String endashTounderscore(String dash){

        if (dash == null || dash.equals("") || dash.trim().equals(""))
            return dash;

        if (dash.contains("-")) {
            dash = dash.replaceAll("-","_");
        }

        return dash;
    }

    public static void main(String[] args) {
        String dash = "reward-app-";
        System.out.printf(endashTounderscore(dash))
        ;
    }
}
