package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * Created on 2015/5/28.
 *
 * @author 金洋
 * @since 2.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParentFirst {
    boolean value() default true;
}
