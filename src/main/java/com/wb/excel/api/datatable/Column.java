package com.wb.excel.api.datatable;


import com.wb.excel.api.enumeration.DataType;
import com.wb.excel.api.util.StringUtil;

import java.io.Serializable;

/**
 * DataTable中的表头，包含本列的一些限制条件。<br/>
 * 现有：列名、是否必须、是否允许重复、说明字段以及数据类型
 * Created by edward on 9/19/14.
 */
public class Column implements Serializable {
    /**
     * 列名
     */
    private String name;
    /**
     * 该列的最大宽度
     */
    private int cellWidth;
    /**
     * 是否为隐藏列，如果为隐藏列，在导出Excel文件时会被忽略
     */
    private boolean isHidden;
    /**
     * 是否是必输列
     */
    private boolean isRequired;
    /**
     * 该列的描述字段
     */
    private String description;
    /**
     * 该列的数据类型
     */
    private DataType dataType;

    public Column() {
        this.name = "";
        this.cellWidth = 1;
        this.isHidden = false;
        this.isRequired = false;
        this.description = "";
        this.dataType = DataType.STRING;
    }

    public Column(String name) {
        this.name = name;
        this.cellWidth = 1;
        this.isHidden = false;
        this.isRequired = false;
        this.description = "";
        this.dataType = DataType.STRING;
    }

    //----------- getter & setter --------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtil.getByteLength(name) > cellWidth) {
            cellWidth = StringUtil.getByteLength(name);
        }
        this.name = name;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
