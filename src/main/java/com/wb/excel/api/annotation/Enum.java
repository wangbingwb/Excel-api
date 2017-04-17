package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * 优先通过target来判断枚举的值
 * Created on 2014/9/26.
 *
 * @author
 * @version v1.0.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Enum {
    public Class target();
}
