package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * 是否允许该列重复。默认为false(不允许重复）
 * Created on 2014/9/24.
 *
 * @author
 * @version v1.0.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IsDuplicated {
    boolean value() default false;
}
