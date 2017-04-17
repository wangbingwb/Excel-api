package com.wb.excel.api.datatable;


import com.wb.excel.api.enumeration.Status;

import java.io.Serializable;

/**
 * 单元格.
 * Created on 2014/9/23.
 *
 * @author
 * @since 0.1.0
 */
public class Cell implements Serializable {
    /**
     * 单元格的状态
     */
    private Status status;

    /**
     * 单元格的值
     */
    private String value;

    /**
     * 默认无参构造方法.会将单元格的状态设为通过.
     */
    public Cell() {
        this.status = Status.PASS;
        this.value = "";
    }

    /**
     * 传入默认单元格值的构造方法.
     *
     * @param value 单元格的值.
     */
    public Cell(String value) {
        this.value = value;
        this.status = Status.PASS;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
