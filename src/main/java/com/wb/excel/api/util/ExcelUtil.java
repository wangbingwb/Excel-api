package com.wb.excel.api.util;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Excel工具类.
 * Created on 2014/9/1.
 *
 * @author
 * @since 0.1.0
 */
public class ExcelUtil {

    /**
     * 获取单元格的值
     *
     * @param cell 要获取值的单元格
     * @return 单元格的值
     */
    public static String getValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(cell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(cell.getStringCellValue());
        }
    }
}
