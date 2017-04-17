package com.wb.excel.api.annotation;

import java.lang.annotation.*;

/**
 * 枚举类的key所对应的value数组。<br/><br/>
 * 导出时：
 * 如果一个字段上出现了多个value，会取出第一个作为导出内容。<br/>
 * 如果未定义值，会把字段名作为导出内容。<br/><br/>
 * <p/>
 * 导入时：
 * 如果value数组中出现了导入的值，返回该value对应的key值。<br/><br/>
 * 但是，如果同一个文件里出现了重复的value，DataTable会默认使用第一个出现该value的Key值<br/>
 * 不能保证第一个出现的地方是你想要的结果，所以可能会返回无法预料的结果。<br/>
 * 所以，强烈建议使用时避免出现重复的内容。<br/><br/>
 * Created on 2014/9/28.
 *
 * @author
 * @version 0.1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumValue {
    String[] value() default "";
}
