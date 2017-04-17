package com.wb.excel.api.entity;

import java.util.Map;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:Excel 集合对象
 * ***************************************************************
 * </pre>
 */
public class ExcelCollectionEntity {
    /**
     * 集合对应的名称
     */
    private String name;
    /**
     * Excel 列名称
     */
    private String excelName;
    /**
     * 实体对象
     */
    private Class<?> type;

    private Map<String, ExcelImportEntity> excelParams;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Map<String, ExcelImportEntity> getExcelParams() {
        return excelParams;
    }

    public void setExcelParams(Map<String, ExcelImportEntity> excelParams) {
        this.excelParams = excelParams;
    }
}
