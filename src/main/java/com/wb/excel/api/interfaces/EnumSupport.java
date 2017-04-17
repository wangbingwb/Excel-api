package com.wb.excel.api.interfaces;

/**
 * 自定义枚举的基础类。
 * Created on 2014/9/27.
 *
 * @author
 * @version 0.1.0
 */
@Deprecated
public interface EnumSupport {
    /**
     * 每个实现类自身都要实现检查方法。<br/>
     * 用于判断输入的值是否属于枚举内容。
     *
     * @param str 要判断的值
     * @return true || false / 符合||不符合
     */
    public Boolean check(String str);

    /**
     * 通过Value来获取Key。<br/>
     * 例如性别： 输入男、Male、male都返回F（F为数据库中存储的值）
     *
     * @param value
     * @return Key。最终存储至数据库中的值。
     */
    public Object getKey(String value);

    /**
     * 通过Key来获取Value<br/>
     * 例如性别：输入F,返回男
     *
     * @param key
     * @return Value。最终要展现给用户看的值。
     */
    public String getValue(Object key);
}
