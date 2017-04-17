package com.wb.excel.api.style;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created on 2015/1/29.
 *
 * @author
 * @since 2.0.0
 */
public class CheckMessageCellStyle extends BaseCellStyle {
    public CheckMessageCellStyle(Workbook workbook) {
        super(workbook);
        Font font = workbook.createFont();                      // 单元格的字体
        font.setColor(HSSFColor.BLACK.index);                   // 字体颜色-黑色
        CellStyle style = workbook.createCellStyle();     // 单元格的样式
        style.setFillPattern(CellStyle.NO_FILL);                // 设置单元格无填充
        style.setFont(font);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  //上下居中
    }
}
