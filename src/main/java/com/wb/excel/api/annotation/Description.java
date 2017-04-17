package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * Created on 2014/9/24.
 *
 * @author
 * @version v1.0.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {
    String value() default "";

}
