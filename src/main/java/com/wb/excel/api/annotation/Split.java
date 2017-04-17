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
public @interface Split {
    /**
     * 分隔字符
     *
     * @return
     */
    String reg() default "";

    /**
     * 取分隔后的结果的下标。从0开始。
     *
     * @return
     */
    int index() default 0;
}
