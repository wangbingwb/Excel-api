package com.wb.excel.api.entity;

import com.wb.excel.api.style.ErrorCellStyle;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:导入返回结果
 * ***************************************************************
 * </pre>
 */
public class ExcelImportResult<T> {
    /**
     * 结果集
     */
    private List<T> list;
    /**
     * 是否存在校验失败
     */
    private boolean verfiyFail;

    private Map<Integer, DataVerifyResult> verifyResult;

    /**
     * 数据源
     */
    private Workbook workbook;
    private CellStyle errorMessageStyle;
    private ErrorCellStyle errorCellStyle;

    public ExcelImportResult(List<T> list, boolean verfiyFail, Workbook workbook, Map<Integer, DataVerifyResult> verifyResult) {
        this.list = list;
        this.verfiyFail = verfiyFail;
        this.workbook = workbook;
        this.verifyResult = verifyResult;
        this.createErrorCellStyle(workbook);
        errorCellStyle = new ErrorCellStyle(workbook);
    }

    public List<T> getList() {
        return list;
    }

    public Workbook getWorkbook() {
        //循环添加错误信息
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        //行信息，添加校验结果
        Row titleRow = rows.next();
        addValidateTitleInfo(titleRow);
        //循环，给行添加错误信息
        Row row = null;
        while (rows.hasNext() && (row == null || sheet.getLastRowNum() - row.getRowNum() > 0)) {
            row = rows.next();
            addValidateInfo(row, verifyResult.get(row.getRowNum()));
        }
        return workbook;
    }

    public byte[] getBytes() throws IOException {
        Workbook tempworkbook = this.getWorkbook();
        if (tempworkbook != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            tempworkbook.write(outputStream);
            return outputStream.toByteArray();
        }
        return null;
    }

    /**
     * 添加校验信息
     *
     * @param row
     */
    private void addValidateTitleInfo(Row row) {
        Map<Integer, String> temptitle = new HashMap<Integer, String>();
        for (int i = row.getFirstCellNum(), le = row.getLastCellNum(); i < le; i++) {
            temptitle.put(i, row.getCell(i).getStringCellValue());
        }
        for (int i = row.getFirstCellNum(), le = row.getLastCellNum(); i < le; i++) {
            row.createCell(i + 1).setCellValue(temptitle.get(i));
        }
        row.createCell(0).setCellValue("检查状态");
        row.createCell(row.getLastCellNum()).setCellValue("错误信息");
    }

    /**
     * 添加错误行信息
     *
     * @param row
     * @param dataVerifyResult
     */
    private void addValidateInfo(Row row, DataVerifyResult dataVerifyResult) {
        Map<Integer, String> temptitle = new HashMap<Integer, String>();
        for (int i = row.getFirstCellNum(), le = row.getLastCellNum(); i < le; i++) {
            temptitle.put(i, String.valueOf(getCellValue(row.getCell(i))));
        }
        for (int i = row.getFirstCellNum(), le = row.getLastCellNum(); i < le; i++) {
            row.createCell(i + 1).setCellValue(temptitle.get(i));
        }
        Boolean result = (dataVerifyResult == null || dataVerifyResult.isSuccess()) ? true : false;
        Cell statusCell = row.createCell(0);
        if (!result) {
            statusCell.setCellStyle(errorCellStyle.getStyle());
        }
        statusCell.setCellValue(result ? "通过" : "不通过");
        Cell errorcell = row.createCell(row.getLastCellNum());
        errorcell.setCellStyle(errorMessageStyle);
        errorcell.setCellValue(dataVerifyResult == null ? "" : dataVerifyResult.getMsg());
    }

    /**
     * 获取列数据，根据列属性
     *
     * @param cell
     * @return
     */
    private Object getCellValue(Cell cell) {
        Object result = null;
        if (cell != null) {
            if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                result = cell.getNumericCellValue();
            } else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
                result = cell.getBooleanCellValue();
            } else {
                result = cell.getStringCellValue();
            }
        }
        result = result == null ? "" : result;
        return result;
    }

    public boolean isVerfiyFail() {
        return verfiyFail;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setVerfiyFail(boolean verfiyFail) {
        this.verfiyFail = verfiyFail;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public void setVerifyResult(Map<Integer, DataVerifyResult> verifyResult) {
        Iterator<Integer> keys = verifyResult.keySet().iterator();
        while (keys.hasNext()) {
            Integer key = keys.next();
            if (this.verifyResult == null) {
                this.verifyResult = new HashMap<>();
            }
            if (this.verifyResult.containsKey(key)) {
                DataVerifyResult result = this.verifyResult.get(key);
                result.setMsg(result.getMsg() + " " + verifyResult.get(key).getMsg());
                result.setSuccess(verifyResult.get(key).isSuccess());
                this.verifyResult.put(key, result);
            } else {
                this.verifyResult.put(key, verifyResult.get(key));
            }
        }
    }

    private void createErrorCellStyle(Workbook workbook) {
        errorMessageStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(Font.COLOR_RED);
        errorMessageStyle.setFont(font);
    }
}
