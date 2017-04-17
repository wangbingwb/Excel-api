package com.wb.excel.api.style;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created on 2015/1/29.
 *
 * @author
 * @since 2.0.0
 */
public class BaseFont {
    /**
     * 字体
     */
    protected Font font;

    public BaseFont(Workbook workbook) {
        font = workbook.createFont();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
}
