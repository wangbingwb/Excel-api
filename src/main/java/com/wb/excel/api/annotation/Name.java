package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * 为字段、方法或类注解名称。<br/>
 * Created on 2014/9/24.
 *
 * @author
 * @version v1.0.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Name {
    String value();

    /**
     * 排序
     *
     * @return
     */
    public String orderNum() default "0";

    /**
     * 日期格式化
     *
     * @return
     */
    public String dateFormat() default "yyyy-MM-dd";
}
