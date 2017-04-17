package com.wb.excel.api.exception;

/**
 * Created on 2014/10/15.
 *
 * @author
 * @version 0.1.0
 */
public class ErrorType {
    /**
     * 唯一性错误，出现了不允许重复的内容
     */
    public static String UNIQUENESS_ERROR = "UNIQUENESS_ERROR";
    /**
     * 期待值为空	找不到想要的对象
     */
    public static String EXPECTATION_NULL = "EXPECTATION_NULL";
    /**
     * 业务错误	不符合业务逻辑的情况发生
     */
    public static String BUSINESS_ERROR = "BUSINESS_ERROR";
    /**
     * 系统错误	JDBC的错误等
     */
    public static String SYSTEM_ERROR = "SYSTEM_ERROR";
    /**
     * 非法的参数	无效，格式不对、非法值、越界等
     */
    public static String INVALID_PARAMETER = "INVALID_PARAMETER";
    /**
     * 其它未归类错误
     */
    public static String OTHER = "OTHER";
    /**
     * 异常信息Dump
     */
    public static String STACK_DUMP = "STACK_DUMP";
}
