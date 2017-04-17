package com.wb.excel.api.enumeration;

import com.wb.excel.api.annotation.Description;
import com.wb.excel.api.annotation.Name;
import com.wb.excel.api.datatable.Cell;
import com.wb.excel.api.util.EnumUtil;
import com.wb.excel.api.util.StringUtil;
import com.wb.excel.api.util.ValidationUtil;

/**
 * 数据类型枚举类。包含了目前DataTable支持验证的11种类型。<br/>
 * DataTable会对指定的类型进行格式上的验证。
 * Created on 2014/9/19/.
 *
 * @author
 * @version v1.0.0.0
 */
public enum DataType {
    @Name("字符型")
    @Description("普通的字符串类型，例如：abc123.,!@#")
    STRING,
    @Name("整数型")
    @Description("整数类型，小数点后的数字会被抹去（不是四舍五入）,例如：11，200，4000")
    NUMBER,
    @Name("数字类型")
    @Description("可以带小数点的数字类型，例如：1.00，1.01")
    DECIMAL,
    @Name("网络地址型")
    @Description("网址，文件地址等。")
    URL,
    @Name("邮箱型")
    @Description("邮箱地址，例如：test@xxx.com , test@xxx.com.cn")
    EMAIL,
    @Name("手机号码型")
    @Description("手机号码。仅限用于大陆手机号中。如：13300010002")
    PHONE,
    @Name("日期型")
    @Description("普通的日期类型，例如：2014-10-01")
    DATE,
    @Name("时间日期型（秒）")
    @Description("时间精确到秒的日期类型，例如：2014-10-01 10:30:00")
    DATETIME,
    @Name("时间日期型（分钟）")
    @Description("时间精确到分钟的日期类型，例如：2014-10-01 10:30")
    DATEMINUTE,
    @Name("是否型")
    @Description("指定是或者否的类型，例如：是，否")
    BOOLEAN,
    @Name("大数型")
    @Description("用于较大的数字类型，会忽略小数点以后的内容")
    LONG;

    /**
     * 匹配格式.
     *
     * @param type  要判断的类型
     * @param cell  需要判断类型的单元格（可能会有改变值的情况发生，所以需要传入此值）
     * @param value 已被简单处理过的需要判断的值
     * @return 匹配结果。true，符合；false，不符合
     */
    public static boolean check(DataType type, Cell cell, String value) {
        boolean typeFlag = true;
        if (value.length() > 0) {
            switch (type) {
                case EMAIL:
                    typeFlag = ValidationUtil.checkEmail(value);
                    break;
                case PHONE:
                    typeFlag = ValidationUtil.checkPhone(value);
                    break;
                case URL:
                    typeFlag = ValidationUtil.checkUrl(value);
                    break;
                case DECIMAL:
                    typeFlag = ValidationUtil.checkDouble(value);
                    break;
                case NUMBER:
                    typeFlag = ValidationUtil.checkInteger(value);
                    if (typeFlag) {
                        cell.setValue(StringUtil.transferInteger(value));
                    }
                    break;
                case DATE:
                    typeFlag = ValidationUtil.checkDate(value);
                    if (typeFlag) {
                        cell.setValue(StringUtil.transferDate(value));
                    }
                    break;
                case DATETIME:
                    typeFlag = ValidationUtil.checkDatetime(value);
                    if (typeFlag) {
                        cell.setValue(StringUtil.transferDatetime(value));
                    }
                    break;
                case DATEMINUTE:
                    typeFlag = ValidationUtil.checkDatetime(value);
                    if (typeFlag) {
                        cell.setValue(StringUtil.transferDateminute(value));
                    }
                    break;
                case BOOLEAN:
                    typeFlag = EnumUtil.check(YesNo.class, value);
                    if (typeFlag) {
                        cell.setValue(StringUtil.transferBoolean(value));
                    }
                    break;
                case LONG:
                    typeFlag = ValidationUtil.checkLong(value);
                    if (typeFlag) {
                        cell.setValue(StringUtil.transferLong(value));
                    }
                    break;
                default:
                    break;
            }
        }
        return typeFlag;
    }
}
