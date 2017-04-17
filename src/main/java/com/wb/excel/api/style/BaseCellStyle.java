package com.wb.excel.api.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created on 2015/1/29.
 *
 * @author
 * @since 2.0.0
 */
public class BaseCellStyle {
    /**
     * 样式
     */
    protected CellStyle style;

    public BaseCellStyle(Workbook workbook) {
        style = workbook.createCellStyle();
    }

    public CellStyle getStyle() {
        return style;
    }

    public void setStyle(CellStyle style) {
        this.style = style;
    }
}
