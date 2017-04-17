package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * Created on 2014/9/26.
 *
 * @author
 * @version v1.0.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Substring {
    int start() default 0;

    int end() default 0;
}
