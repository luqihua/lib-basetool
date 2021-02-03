package com.lu.tool.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: luqihua
 * Time: 2017/10/11
 * Description: 常用正则工具
 */

public class RegularUtil {
    /**
     * 判断一个字符串是否是电话号码
     *
     * @param str
     * @return
     */
    public static boolean isTelephoneNumber(String str) {
        String reg = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[579]))\\d{8}";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(str).matches();
    }

    /**
     * 是否是银行卡号
     *
     * @param str
     * @return
     */
    public static boolean isBankNumber(String str) {
        String reg = "\\d{16,21}";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(str).matches();
    }

    public static boolean isEmail(String str) {
        String reg = "^[a-zA-Z0-9]+([a-zA-Z0-9]+[-_.]?)*@([a-zA-Z0-9]+[-.]?)*[a-zA-Z0-9]+\\.[a-zA-Z0-9]{2,5}$";
        return Pattern.compile(reg).matcher(str).matches();
    }

    /**
     * 是否是ip
     *
     * @param address
     * @return
     */
    public static boolean isIPAddress(String address) {
        if (TextUtils.isEmpty(address)) return false;
        String reg = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        return Pattern.compile(reg).matcher(address).matches();
    }

    /**
     * 是否是正整数
     *
     * @param str
     * @return
     */
    public static boolean isIntegerMoneyNumber(String str) {
        String reg = "^([1-9][0-9]*)$";
        return Pattern.compile(reg).matcher(str).matches();
    }

    /**
     * 是否是 100 的整数倍
     *
     * @param str
     */
    public static boolean isMultipleOf100(String str) {
        String reg = "^[1-9]\\d*(00)$";
        return Pattern.compile(reg).matcher(str).matches();
    }


    /**
     * 是否是密码
     *
     * @param str
     * @return
     */
    public static boolean isPassword(String str) {
        if (str == null || str.length() < 6 || str.length() > 16) return false;
        String reg = "[a-zA-Z0-9]*(([a-zA-z][0-9])|([0-9][a-zA-z]))[a-zA-Z0-9]*";
        return Pattern.compile(reg).matcher(str).matches();
    }

}
