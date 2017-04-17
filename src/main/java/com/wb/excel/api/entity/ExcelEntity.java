package com.wb.excel.api.entity;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:
 * ***************************************************************
 * </pre>
 */
public class ExcelEntity {
    /**
     * 参数名称
     */
    protected String filedName;
    /**
     * 对应name
     */
    protected String showname;
    /**
     * 对应的枚举类型
     */
    protected Class Enum;
    /**
     * 对应的数据格式化
     */
    protected String split;

    /**
     * 注释
     */
    protected String description;
    /**
     * 是否重复
     */
    protected Boolean isDuplicated;
    /**
     * 日期格式化
     */
    protected String dateFormat = "yyyy-MM-dd";

    /**
     * set/get方法
     */
    private Method method;
    private List<Method> methods;

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public Class getEnum() {
        return Enum;
    }

    public void setEnum(Class anEnum) {
        Enum = anEnum;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }


    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsDuplicated() {
        return isDuplicated;
    }

    public void setIsDuplicated(Boolean isDuplicated) {
        this.isDuplicated = isDuplicated;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
