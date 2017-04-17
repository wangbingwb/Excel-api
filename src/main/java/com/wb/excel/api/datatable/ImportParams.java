package com.wb.excel.api.datatable;

import com.wb.excel.api.interfaces.IExcelVerifyHandler;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:导入参数设置
 * ***************************************************************
 * </pre>
 */
public class ImportParams {
//    /**
//     * 表格标题行数,默认0
//     */
//    private int                 titleRows        = 0;
//    /**
//     * 表头行数,默认1
//     */
//    private int                 headRows         = 1;
//    /**
//     * 字段真正值和列标题之间的距离 默认0
//     */
//    private int                 startRows        = 0;
//    /**
//     * 主键设置,如何这个cell没有值,就跳过 或者认为这个是list的下面的值
//     */
//    private int                 keyIndex         = 0;
    /**
     * 上传表格需要读取的sheet 数量,默认为1
     */
    private int sheetNum = 1;

    /**
     * 校验处理接口
     */
    private IExcelVerifyHandler verifyHanlder;
//    /**
//     * 最后的无效行数
//     */
//    private int                 lastOfInvalidRow = 0;


    public int getSheetNum() {
        return sheetNum;
    }

    public void setSheetNum(int sheetNum) {
        this.sheetNum = sheetNum;
    }

    public IExcelVerifyHandler getVerifyHanlder() {
        return verifyHanlder;
    }

    public void setVerifyHanlder(IExcelVerifyHandler verifyHanlder) {
        this.verifyHanlder = verifyHanlder;
    }

}
