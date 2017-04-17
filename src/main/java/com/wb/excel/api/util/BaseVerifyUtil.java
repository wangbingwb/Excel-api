package com.wb.excel.api.util;


import com.wb.excel.api.entity.DataVerifyResult;

import java.util.regex.Pattern;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:数据基础校验
 * ***************************************************************
 * </pre>
 */
public class BaseVerifyUtil {
    private static String NOT_NULL = "不允许为空";
    private static String IS_MOBILE = "不是手机号";
    private static String IS_TEL = "不是电话号码";
    private static String IS_EMAIL = "不是邮箱地址";
    private static String MIN_LENGHT = "小于规定长度";
    private static String MAX_LENGHT = "超过规定长度";

    private static Pattern mobilePattern = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$");

    private static Pattern telPattern = Pattern.compile("^([0][1-9]{2,3}-)?[0-9]{5,10}$");

    private static Pattern emailPattern = Pattern
            .compile("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$");


    /**
     * email校验
     *
     * @param name
     * @param val
     * @return
     */
    public static DataVerifyResult isEmail(String name, Object val) {
        if (!emailPattern.matcher(String.valueOf(val)).matches()) {
            return new DataVerifyResult(false, name + IS_EMAIL);
        }
        return new DataVerifyResult(true);
    }

    /**
     * 手机校验
     *
     * @param name
     * @param val
     * @return
     */
    public static DataVerifyResult isMobile(String name, Object val) {
        if (!mobilePattern.matcher(String.valueOf(val)).matches()) {
            return new DataVerifyResult(false, name + IS_MOBILE);
        }
        return new DataVerifyResult(true);
    }

    /**
     * 电话校验
     *
     * @param name
     * @param val
     * @return
     */
    public static DataVerifyResult isTel(String name, Object val) {
        if (!telPattern.matcher(String.valueOf(val)).matches()) {
            return new DataVerifyResult(false, name + IS_TEL);
        }
        return new DataVerifyResult(true);
    }

    /**
     * 最大长度校验
     *
     * @param name
     * @param val
     * @return
     */
    public static DataVerifyResult maxLength(String name, Object val, int maxLength) {
        if (notNull(name, val).isSuccess() && String.valueOf(val).length() > maxLength) {
            return new DataVerifyResult(false, name + MAX_LENGHT);
        }
        return new DataVerifyResult(true);
    }

    /**
     * 最小长度校验
     *
     * @param name
     * @param val
     * @param minLength
     * @return
     */
    public static DataVerifyResult minLength(String name, Object val, int minLength) {
        if (notNull(name, val).isSuccess() && String.valueOf(val).length() < minLength) {
            return new DataVerifyResult(false, name + MIN_LENGHT);
        }
        return new DataVerifyResult(true);
    }

    /**
     * 非空校验
     *
     * @param name
     * @param val
     * @return
     */
    public static DataVerifyResult notNull(String name, Object val) {
        if (val == null || val.toString().equals("")) {
            return new DataVerifyResult(false, name + NOT_NULL);
        }
        return new DataVerifyResult(true);
    }

    /**
     * 正则表达式校验
     *
     * @param name
     * @param val
     * @param regex
     * @param regexTip
     * @return
     */
    public static DataVerifyResult regex(String name, Object val, String regex,
                                         String regexTip) {
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(String.valueOf(val)).matches()) {
            return new DataVerifyResult(false, name + regexTip);
        }
        return new DataVerifyResult(true);
    }
}
