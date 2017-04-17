package com.wb.excel.api.style;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created on 2015/1/29.
 *
 * @author
 * @since 2.0.0
 */
public class CheckFailureCellStyle extends BaseCellStyle {
    public CheckFailureCellStyle(Workbook workbook) {
        super(workbook);
        Font font = workbook.createFont();                      // 单元格的字体
        font.setColor(HSSFColor.WHITE.index);                   // 字体颜色-白色
        CellStyle style = workbook.createCellStyle();           // 单元格的样式
        style.setFillForegroundColor(HSSFColor.RED.index);    // 背景颜色-红色
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);       // 设置单元格填充样式
        style.setAlignment(CellStyle.ALIGN_CENTER);             // 居中
        style.setFont(font);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  //上下居中

        //下边框
        style.setBorderBottom(CellStyle.SOLID_FOREGROUND);
        //下边框颜色
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        //左边框
        style.setBorderLeft(CellStyle.SOLID_FOREGROUND);
        //左边框颜色
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        //右边框
        style.setBorderRight(CellStyle.SOLID_FOREGROUND);
        //右边框颜色
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //上边框
        style.setBorderTop(CellStyle.SOLID_FOREGROUND);
        //上边框颜色
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }
}
