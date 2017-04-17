package com.wb.excel.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description: 导入导出接合
 * ***************************************************************
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelCollection {
    /**
     * 名称
     *
     * @return
     */
    public String value();

    /**
     * 所属的排序
     *
     * @return
     */
    public String orderNum() default "0";

    /**
     * 创建时创建的类型 默认值是 arrayList
     */
    public Class<?> type() default ArrayList.class;
}
