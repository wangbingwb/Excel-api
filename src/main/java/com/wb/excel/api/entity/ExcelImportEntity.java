package com.wb.excel.api.entity;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:导入的实体信息
 * ***************************************************************
 * </pre>
 */
public class ExcelImportEntity extends ExcelEntity {
    /**
     * 校驗參數
     */
    private ExcelVerifyEntity verify;

    /**
     * 校验参数名称
     */
//    private String verifyFieldName ;
    public ExcelVerifyEntity getVerify() {
        return verify;
    }

    public void setVerify(ExcelVerifyEntity verify) {
        this.verify = verify;
    }

//    public String getVerifyFieldName() {
//        return verifyFieldName;
//    }
//
//    public void setVerifyFieldName(String verifyFieldName) {
//        this.verifyFieldName = verifyFieldName;
//    }
}
