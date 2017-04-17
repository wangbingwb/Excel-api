package com.wb.excel.api;

import com.wb.excel.api.annotation.Description;
import com.wb.excel.api.datatable.Cell;
import com.wb.excel.api.datatable.Column;
import com.wb.excel.api.datatable.DataTable;
import com.wb.excel.api.enumeration.DataType;
import com.wb.excel.api.enumeration.Status;
import com.wb.excel.api.style.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Excel帮助类.
 * <p/>
 * Created on 2014/9/27.
 *
 * @author
 * @since 0.1.0
 */
public class Excel {
    /**
     * Excel表格
     */
    private XSSFWorkbook workbook;

    /**
     * 表头样式
     */
    private CellStyle headCellStyle;

    /**
     * 错误单元格的样式
     */
    private CellStyle errorCellStyle;

    /**
     * 错误的数字单元格的样式(数字靠右)
     */
    private CellStyle errorNumberCellStyle;

    /**
     * 普通单元格的样式
     */
    private CellStyle normalCellStyle;

    /**
     * 普通数字单元格的样式
     */
    private CellStyle normalNumberCellStyle;

    /**
     * 检查消息单元格的样式(最右边一列)
     */
    private CellStyle checkMessageCellStyle;

    /**
     * 检查结果失败的单元格的样式
     */
    private CellStyle checkFailureCellStyle;

    /**
     * 检查结果通过的单元格的样式
     */
    private CellStyle checkSuccessCellStyle;

    /**
     * 正常的文字
     */
    private Font normalFont;

    /**
     * 红色文字
     */
    private Font redFont;

    /**
     * 初始化一个Excel文件.
     */
    public void init() {
        workbook = new XSSFWorkbook();

        //---------- 创建样式 ------------------
        headCellStyle = new HeadCellStyle(workbook).getStyle();
        errorCellStyle = new ErrorCellStyle(workbook).getStyle();
        errorNumberCellStyle = new ErrorNumberCellStyle(workbook).getStyle();
        normalCellStyle = new NormalCellStyle(workbook).getStyle();
        normalNumberCellStyle = new NormalNumberCellStyle(workbook).getStyle();
        checkMessageCellStyle = new CheckMessageCellStyle(workbook).getStyle();
        checkFailureCellStyle = new CheckFailureCellStyle(workbook).getStyle();
        checkSuccessCellStyle = new CheckSuccessCellStyle(workbook).getStyle();

        //----------- 创建字体 ----------------
        normalFont = new NormalFont(workbook).getFont();
        redFont = new RedFont(workbook).getFont();
    }

    /**
     * 默认无参构造方法.
     */
    public Excel() {
        init();
    }

    /**
     * 将一个DataTable转换为Excel对象,不显示检查结果和错误消息栏.<br/>
     * 通常用于导出报表.
     * {@link Excel#Excel(boolean flag, DataTable... dataTable)}
     *
     * @param tables 要导出的DataTable集合
     */
    public Excel(DataTable... tables) {
        workbook = this.initExcel(false, tables);
    }

    /**
     * 将一个DataTable转换为Excel对象.
     *
     * @param table DataTable
     * @param flag  是否需要添加检查结果和错误消息栏（true，会添加；false，不会添加）
     */
    @Deprecated
    public Excel(DataTable table, boolean flag) {
        workbook = this.initExcel(flag, table);
    }

    /**
     * 将若干DataTable转换为Excel对象.
     *
     * @param flag   是否需要添加检查结果和错误消息栏（true，会添加；false，不会添加）
     * @param tables DataTable列表
     */
    public Excel(boolean flag, DataTable... tables) {
        workbook = this.initExcel(flag, tables);
    }

    public XSSFWorkbook initExcel(boolean flag, DataTable... tables) {
        init();
        Set<String> nameSet = new HashSet<>(tables.length);
        for (DataTable table : tables) {
            String name = table.getName();
            int index = 1;
            while (nameSet.contains(name)) {
                name = table.getName() + "(" + index + ")";
                index++;
            }
            nameSet.add(name);

            //创建一个Sheet表
            XSSFSheet sheet = workbook.createSheet(name);
            sheet.setDefaultRowHeightInPoints(20);
            Row firstRow = sheet.createRow(0); // 下标为0的行开始
            XSSFDrawing drawing = sheet.createDrawingPatriarch();

            //----------------表头--------------------
            if (flag) {
                // 检查结果栏
                org.apache.poi.ss.usermodel.Cell firstCell = firstRow.createCell(0);
                String columnName = DataTable.CHECK_STATUS_NAME;
                firstCell.setCellStyle(headCellStyle);
                firstCell.setCellValue(new XSSFRichTextString(columnName));
                sheet.setColumnWidth(0, (4 + 8) * 256);
            }
            // 数据栏
            int hiddenNumber = 0;
            for (int j = 0; j < table.getColumnIndex(); j++) {
                Column column = table.getColumns()[j];
                if (column.isHidden()) {
                    hiddenNumber++;
                    continue;
                }
                int k = j - hiddenNumber;
                if (flag) {
                    k++;
                }
                org.apache.poi.ss.usermodel.Cell firstCell = firstRow.createCell(k);
                String columnName = column.getName();
                XSSFRichTextString textString;
                if (column.isRequired()) {
                    textString = new XSSFRichTextString("*" + columnName);
                    textString.applyFont(0, 1, redFont);
                    textString.applyFont(1, textString.length(), normalFont);
                } else {
                    textString = new XSSFRichTextString(columnName);
                    textString.applyFont(normalFont);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(column.getDescription()).append("\n");
                if (column.getDataType() != DataType.STRING) {
                    // 如果数据类型不是字符串类型，添加特殊数据类型的说明信息。
                    Field[] fields = DataType.class.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equals(column.getDataType().name())) {
                            if (field.isAnnotationPresent(Description.class)) {

                                // 获取声明字段上的Description信息。
                                Description description = field.getAnnotation(Description.class);
                                if (description.value() != null) {
                                    sb.append(description.value());
                                }
                            }
                        }
                    }
                }

                // 如果填写了注释信息
                if (sb.length() > 1) {
                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6);
                    XSSFComment comment = drawing.createCellComment(anchor);
                    comment.setString(sb.toString());
                    firstCell.setCellComment(comment);
                }
                firstCell.setCellValue(textString);
                firstCell.setCellStyle(headCellStyle);
                sheet.setColumnWidth(k, (4 + column.getCellWidth()) * 256);
            }

            // 错误消息栏
            if (flag) {
                org.apache.poi.ss.usermodel.Cell firstCell = firstRow.createCell(table.getColumnIndex() + 1 - hiddenNumber);
                String columnName = DataTable.CHECK_STATUS_RESULT;
                firstCell.setCellStyle(headCellStyle);
                firstCell.setCellValue(new XSSFRichTextString(columnName));
                sheet.setColumnWidth(table.getColumnIndex() + 1, (4 + 10) * 256);
            }

            // 冻结第一行
            //sheet.createFreezePane(255,1);

            //------------------数据--------------------
            for (int i = 0; i < table.getRowIndex(); i++) {
                Row row = sheet.createRow(i + 1);
                boolean rowFlag = true;

                //如果该行有错误
                if (flag) {
                    org.apache.poi.ss.usermodel.Cell xssfCell = row.createCell(0);
                    if (table.getRowError(i) != null) {
                        rowFlag = false;
                        xssfCell.setCellValue("不通过");
                        xssfCell.setCellStyle(checkFailureCellStyle);
                    } else {
                        xssfCell.setCellValue("通过");
                        xssfCell.setCellStyle(checkSuccessCellStyle);
                    }
                }

                int j = 0;
                hiddenNumber = 0;
                for (; j < table.getColumnIndex(); j++) {
                    Column column = table.getColumns()[j];
                    if (column.isHidden()) {
                        hiddenNumber++;
                        continue;
                    }
                    int k = j - hiddenNumber;
                    if (flag) {
                        k++;
                    }

                    org.apache.poi.ss.usermodel.Cell xssfCell = row.createCell(k);
                    Cell cell = table.getCell(i, j);
                    if (null == cell) {
                        continue;
                    }
                    String value = cell.getValue();
                    xssfCell.setCellValue(value);

                    // 如果该列是数字类型,则靠右
                    if (table.getColumns()[j].getDataType() == DataType.DECIMAL
                            || table.getColumns()[j].getDataType() == DataType.NUMBER
                            || table.getColumns()[j].getDataType() == DataType.LONG) {
                        if (flag && !cell.getStatus().equals(Status.PASS)) {
                            xssfCell.setCellStyle(errorNumberCellStyle);
                        } else {
                            xssfCell.setCellStyle(normalNumberCellStyle);
                        }
                    } else {
                        if (flag && !cell.getStatus().equals(Status.PASS)) {
                            xssfCell.setCellStyle(errorCellStyle);
                        } else {
                            xssfCell.setCellStyle(normalCellStyle);
                        }
                    }
                }

                if (flag && !rowFlag) {
                    org.apache.poi.ss.usermodel.Cell xssfCell = row.createCell(j + 1 - hiddenNumber);
                    xssfCell.setCellValue(table.getRowError(i));
                    xssfCell.setCellStyle(checkMessageCellStyle);
                }
            }
        }
        return workbook;
    }

    /**
     * 将一个DataTable转换为Excel对象
     *
     * @param table DataTable
     * @param flag  是否需要添加检查结果和错误消息栏
     * @return Excel的Workbook对象
     */
    @Deprecated
    public XSSFWorkbook initExcel(DataTable table, boolean flag) {
        return initExcel(flag, table);
    }

    /**
     * 得到已生成好的Excel文件的字节流信息
     *
     * @return 字节流信息
     * @throws IOException
     */
    public byte[] getBytes() throws IOException {
        if (workbook != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
        return null;
    }

    //------------ getter & setter --------------------
    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }
}
