package com.wb.excel.api.enumeration;

import com.wb.excel.api.annotation.EnumValue;

/**
 * 用于判断Boolean型的值。仅供DataTable判断Boolean类型时使用。<br/><br/>
 * <b>不建议使用。</b><br/><br/>
 * Created on 2014/9/27.
 *
 * @author
 * @version 0.1.0
 */
public enum YesNo {
    @EnumValue({"是", "yes", "y", "true"})
    Y,
    @EnumValue({"否", "no", "n", "false"})
    N
}
