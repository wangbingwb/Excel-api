package com.wb.excel.api.enumeration;

/**
 * Created on 2014/9/23.
 *
 * @author
 * @version v1.0.0.0
 */
public enum Status {
    /**
     * 数据库中已存在
     */
    EXIST,
    /**
     * 数据库中不存在
     */
    NOTEXIST,
    /**
     * 不符合的枚举值
     */
    ERROR_VALUE,
    /**
     * 验证通过
     */
    PASS,
    /**
     * 不允许重复的列发生了重复
     */
    REPEAT,
    /**
     * 格式不正确
     */
    FORMAT,
    /**
     * 长度不符合要求
     */
    LENGTH,
    /**
     * 不允许为空的列出现了空
     */
    EMPTY,
    /**
     * 数字类型的值过小
     */
    TOO_SMALL,
    /**
     * 数字类型的值过大
     */
    TOO_BIG
}
