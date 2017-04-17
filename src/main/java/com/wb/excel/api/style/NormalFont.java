package com.wb.excel.api.style;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 普通字体.颜色黑
 * Created on 2015/1/29.
 *
 * @author
 * @since 2.0.0
 */
public class NormalFont extends BaseFont {
    public NormalFont(Workbook workbook) {
        super(workbook);
        font.setColor(HSSFColor.BLACK.index);               //字体颜色-黑色
    }
}
