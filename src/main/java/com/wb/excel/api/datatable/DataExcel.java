package com.wb.excel.api.datatable;


import com.wb.excel.api.entity.ExcelImportResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:
 * ***************************************************************
 * </pre>
 */
public class DataExcel {
//    static Logger LOGGER = Logger.getLogger(DataExcel.class);

    /**
     * @param file
     * @param pojoClass
     * @param <T>
     * @return
     */
    public static <T> ExcelImportResult<T> importExcel(File file, Class<?> pojoClass, ImportParams importParams) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return new ExcelImport().importExcelByIs(in, pojoClass, importParams);
        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
//                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }


    /**
     * Excel 导入 数据源IO流,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param inputstream
     * @param pojoClass
     * @return
     * @throws Exception
     */
    public static <T> ExcelImportResult<T> importExcel(InputStream inputstream, Class<?> pojoClass, ImportParams importParams) throws Exception {
        return new ExcelImport().importExcelByIs(inputstream, pojoClass, importParams);
    }


}
