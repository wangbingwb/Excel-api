package com.wb.excel.api.enumeration;

import com.wb.excel.api.annotation.EnumValue;

/**
 * Created on 2014/9/26.
 *
 * @author
 * @version 0.1.0
 */
public enum Gender {
    @EnumValue({"男", "male", "m"})
    M,
    @EnumValue({"女", "female", "f"})
    F,
    @EnumValue({"未知", "其它", "其他", "u"})
    U
}
