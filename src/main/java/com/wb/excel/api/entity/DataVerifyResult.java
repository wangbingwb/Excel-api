package com.wb.excel.api.entity;

import java.io.Serializable;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:数据校验结果
 * ***************************************************************
 * </pre>
 */
public class DataVerifyResult implements Serializable {
    /**
     * 是否正确
     */
    private boolean success;
    /**
     * 错误信息
     */
    private String msg;

    public DataVerifyResult() {
    }

    public DataVerifyResult(boolean issuccess) {
        this.success = issuccess;
    }

    public DataVerifyResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
