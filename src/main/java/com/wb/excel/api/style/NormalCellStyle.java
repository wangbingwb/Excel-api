package com.wb.excel.api.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created on 2015/1/29.
 *
 * @author
 * @since 2.0.0
 */
public class NormalCellStyle extends BaseCellStyle {
    public NormalCellStyle(Workbook workbook) {
        super(workbook);
        style.setFillPattern(CellStyle.NO_FILL);            //单元格不填充
        //style.setFont(font);                                //设置单元格字体
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);      //上下居中
        style.setBorderBottom(CellStyle.SOLID_FOREGROUND);          //下边框
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //下边框颜色
        style.setBorderLeft(CellStyle.SOLID_FOREGROUND);            //左边框
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());   //左边框颜色
        style.setBorderRight(CellStyle.SOLID_FOREGROUND);           //右边框
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());  //右边框颜色
        style.setBorderTop(CellStyle.SOLID_FOREGROUND);             //上边框
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());    //上边框颜色
    }
}
