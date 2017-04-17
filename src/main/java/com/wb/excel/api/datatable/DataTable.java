package com.wb.excel.api.datatable;

import com.wb.excel.api.annotation.*;
import com.wb.excel.api.annotation.Enum;
import com.wb.excel.api.enumeration.DataType;
import com.wb.excel.api.enumeration.Status;
import com.wb.excel.api.exception.*;
import com.wb.excel.api.exception.Error;
import com.wb.excel.api.util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据表的定义。<br/>
 * Created on 2014/09/19.
 *
 * @author 沈振家
 * @since 0.1.0
 */
public class DataTable<T> implements Serializable, Cloneable {

    public final static String CHECK_STATUS_NAME = "检查状态";
    public final static String CHECK_STATUS_RESULT = "结果消息";

    /**
     * 初始化时最大的行数
     */
    private int MAX_ROW_NUMBER = 100;

    /**
     * 初始化时最大的列数
     */
    private int MAX_COLUMN_NUMBER = 20;

    /**
     * 表名
     */
    private String name;

    /**
     * 表头的集合
     */
    private Column[] columns;

    /**
     * 当前的行数
     */
    private int rowIndex;
    private int columnIndex;

    /**
     * 单元格里存放的对象
     */
    private Cell[][] data;

    /**
     * 整个Table的错误列表
     */
    private List<Error> errorList;

    /**
     * 每一行的错误情况。
     */
    private List<ArrayList<Error>> errorLists;

    /**
     * 初始化准备工作。在构造方法前调用。
     */
    public void init() {
        rowIndex = 0;
        columnIndex = 0;

        columns = new Column[MAX_COLUMN_NUMBER];
        data = new Cell[MAX_ROW_NUMBER][MAX_COLUMN_NUMBER];

        errorList = new ArrayList<>();
        errorLists = new ArrayList<>(MAX_ROW_NUMBER);
        for (int i = 0; i < MAX_ROW_NUMBER; i++) {
            errorLists.add(new ArrayList<Error>());
        }
    }

    /**
     * 默认构造方法。
     */
    public DataTable() {
        init();
    }

    /**
     * 根据输入的模板类打印下载模板。
     *
     * @param clazz 模板类
     */
    public DataTable(Class<T> clazz) {
        init();
        setColumns(clazz);
    }

    /**
     * 根据模板类对DataTable添加相应的列。
     *
     * @param clazz 模板类
     * @return 包含@Name标记的字段Set
     */
    private Set<Field> setColumns(Class<?> clazz) {
        Name tempName = clazz.getAnnotation(Name.class);
        ParentFirst parentFirstAnnotation = clazz.getAnnotation(ParentFirst.class);
        boolean parentFirst = parentFirstAnnotation != null && parentFirstAnnotation.value();
        if (tempName != null) {
            this.setName(tempName.value());
        } else {
            this.setName(clazz.getName());
        }
        //Field [] fields = clazz.getDeclaredFields();
        Field[] fields = ClassUtil.getFields(clazz, parentFirst);
        Set<Field> set = new HashSet<>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Name.class)) {
                continue;
            }
            set.add(field);
            Name columnName = field.getAnnotation(Name.class);
            Column column = new Column(columnName.value());
            if (field.isAnnotationPresent(NotNull.class)) {
                column.setRequired(true);
            }

            if (field.isAnnotationPresent(Type.class)) {
                Type type = field.getAnnotation(Type.class);
                column.setDataType(type.value());
            } else {
                column.setDataType(DataType.STRING);
            }

            if (field.isAnnotationPresent(Description.class)) {
                column.setDescription(field.getAnnotation(Description.class).value());
            }

            this.addColumn(column);
        }
        return set;
    }

    /**
     * 此构造方法仅用于转换对象列表。<br/>
     * 并不会对数据的格式和合法性进行检验。
     *
     * @param list 需要导出的对象列表
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public DataTable(List<T> list)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        init();
        if (list == null || list.size() == 0) {
            throw new IllegalParameterException("不允许传入空的列表");
        }
        if (list.size() > 0) {
            T tClass = list.get(0);
            Set<Field> set = setColumns(tClass.getClass());

            for (T t : list) {
                DataRow row = new DataRow();
                for (Field field : set) {

                    // 获取字段上的名称注解(作为列名使用)
                    Name fieldName = field.getAnnotation(Name.class);
                    String att = StringUtil.upperFirstWord(field.getName());
                    Method method = t.getClass().getMethod("get" + att);
                    Object value = method.invoke(t);
                    if (null == value) {
                        row.put(fieldName.value(), new Cell());
                    } else {
                        if (field.isAnnotationPresent(Type.class)) {
                            Type type = field.getAnnotation(Type.class);
                            switch (type.value()) {
                                case DATE: {
                                    Date date = (Date) value;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    value = sdf.format(date);
                                    break;
                                }
                                case DATETIME: {
                                    Date date = (Date) value;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    value = sdf.format(date);
                                    break;
                                }
                                case DATEMINUTE: {
                                    Date date = (Date) value;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    value = sdf.format(date);
                                    break;
                                }
                                case NUMBER:
                                    value = StringUtil.transferInteger(value.toString());
                                    break;
                                case LONG:
                                    value = StringUtil.transferLong(value.toString());
                                    break;
                                case BOOLEAN:
                                    value = StringUtil.transferBoolean(value.toString());
                                    break;
                                default:
                                    break;
                            }
                        }

                        // 如果该变量包含枚举标签，处理枚举
                        if (field.isAnnotationPresent(Enum.class)) {
                            Enum e = field.getAnnotation(Enum.class);
                            value = EnumUtil.getValue(e.target(), value.toString());
                        }
                        row.put(fieldName.value(), new Cell(value.toString()));
                    }
                }
                this.addRow(row);
            }
        }
    }

    /**
     * 构造方法。传入Excel流，不需要指定类型。同时，也不会对数据进行检查。
     *
     * @param bytes Excel的字节数组。
     * @throws IOException 可能会出现的错误。通常为以下错误：<br/>
     *                     1.流不能转换为Workbook
     */
    @Deprecated
    public DataTable(byte[] bytes) throws IOException, TemplateNotMatchException {
        init();
        InputStream inputStream = null;         //文件输入流
        Workbook workbook = null;               //导入的文档
        boolean flag;           //用于判断文件的类型
        try {
            flag = true;
            inputStream = new ByteArrayInputStream(bytes);
            workbook = new HSSFWorkbook(inputStream);
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            try {
                flag = true;
                inputStream = new ByteArrayInputStream(bytes);
                workbook = new XSSFWorkbook(inputStream);
            } catch (Exception e) {
                flag = false;
            }
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (!flag) {
            throw new TemplateNotMatchException("不支持的文件类型");
        }

		/* 创建导入表的对象 */
        Sheet sheet = workbook.getSheetAt(0);     //默认只处理第一个Sheet表
        this.setName(sheet.getSheetName());

		/* 读取表头 */
        Row headRow = sheet.getRow(0);
        int columnNum = headRow.getPhysicalNumberOfCells();
        for (int i = 0; i < columnNum; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headRow.getCell(i);
            Column column = new Column();
            column.setName(ExcelUtil.getValue(cell));
            this.addColumn(column);
        }

		/* 逐行读取导入文件的数据，从1开始是因为第0行为表头 */
        int rowNumber = sheet.getLastRowNum();
        for (int i = 1; i <= rowNumber; i++) {
            Row inputRow = sheet.getRow(i);
            DataRow row = new DataRow();
            if (null != inputRow) {
                for (int j = 0; j < columnNum; j++) {
                    org.apache.poi.ss.usermodel.Cell excelCell = inputRow.getCell(j);
                    if (null != excelCell) {
                        row.put(this.getColumns()[j].getName(), new Cell(ExcelUtil.getValue(excelCell)));
                    }
                }
            }
            this.addRow(row);
        }
    }

    /**
     * 以Excel文件的字节数组为数据源，为自己赋值的同时，与指定的模板类进行数据校验。<br/>
     * 如果匹配过程中发现了不符合的数据，会在对应的单元中添加错误信息。<br/>
     *
     * @param bytes Excel文件的字节数组
     * @param clazz 类型
     * @throws IOException               <br/>1:输入流无法转为Excel文件 - 请检查输入的 bytes 是否正确；<br/>
     *                                   2:输入流关闭出错；<br/>
     * @throws TemplateNotMatchException <br/>1:Excel文件的列数与对象类型不匹配 - 请检查Excel表与模板类是否一致；<br/>
     *                                   2:获取枚举类型出错 - 你在模板类中标注了一个变量的值为枚举类型，但程序寻找枚举类时出错；<br/>
     */
    public DataTable(byte[] bytes, Class<T> clazz) throws IOException, TemplateNotMatchException {
        init();
        Workbook workbook = null;
        InputStream is = null;
        boolean flag;
        try {
            flag = true;
            is = new ByteArrayInputStream(bytes);   //读取文件流
            workbook = new HSSFWorkbook(is);
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            try {
                flag = true;
                is = new ByteArrayInputStream(bytes);   //读取文件流
                workbook = new XSSFWorkbook(is);
            } catch (Exception e) {
                flag = false;
            }
        }
        if (is != null) {
            is.close();
        }
        if (!flag) {
            throw new TemplateNotMatchException("不支持的文件类型");
        }

        Sheet sheet = workbook.getSheetAt(0);           //处理第一张Sheet表
        String tableName = sheet.getSheetName();            //DataTable的名字

		/* 获取指定类所有带有Name注解的属性集合 */
        Name className = clazz.getAnnotation(Name.class);   //该类所声明的名字
        ParentFirst parentFirstAnnotation = clazz.getAnnotation(ParentFirst.class);
        boolean parentFirst = parentFirstAnnotation != null && parentFirstAnnotation.value();
        if (className != null) {
            tableName = className.value();
        }
        this.setName(tableName);                            //将类名设为表名，如果类名不存在，将Excel表名设为表名

        //Field[] fields = clazz.getDeclaredFields();         //该类所声明的全部属性
        Field[] fields = ClassUtil.getFields(clazz, parentFirst);         //该类所声明的全部属性
        Set<Field> set = new HashSet<>();                   //用于保存所有带有Name注解的属性
        for (Field field : fields) {
            if (field.isAnnotationPresent(Name.class)) {
                set.add(field);
            }
        }

		/* 读取表头 */
        Row headRow = sheet.getRow(0);
        int columnSum = headRow.getPhysicalNumberOfCells();     //获取Excel列的总数
        int columnMatchNumber = 0;                              //匹配列的数量。用于判断Excel是否包含所有必须列。

		/* 为Excel表中的每一列分配空间 */
        List<Set<String>> sets = new ArrayList<>();
        Type[] types = new Type[columnSum];
        Enum[] enums = new Enum[columnSum];
        Split[] splits = new Split[columnSum];
        Length[] lengths = new Length[columnSum];
        NotNull[] notNulls = new NotNull[columnSum];
        Substring[] substrings = new Substring[columnSum];
        DecimalMin[] decimalMins = new DecimalMin[columnSum];
        DecimalMax[] decimalMaxs = new DecimalMax[columnSum];
        Description[] descriptions = new Description[columnSum];
        IsDuplicated[] isDuplicateds = new IsDuplicated[columnSum];

        boolean[] matchFlag = new boolean[set.size()];  //保存字段是否出现在Excel文件中。

		/* 查找匹配列的数量 */
        for (int i = 0; i < columnSum; i++) {

            sets.add(new HashSet<String>());

            org.apache.poi.ss.usermodel.Cell cell = headRow.getCell(i);
            String headValue = ExcelUtil.getValue(cell);        //获取列名
            headValue = headValue.replace("*", "");
            headValue = headValue.replace(" ", "");

            int tempFieldIndex = 0;                  //字段的编号，临时变量
            for (Field field : set) {
                Name fieldName = field.getAnnotation(Name.class);
                if (headValue.equals(fieldName.value())) {       //如果Excel列名与Class属性名相一致
                    columnMatchNumber++;                       //标记该列的存在
                    types[i] = field.getAnnotation(Type.class);
                    enums[i] = field.getAnnotation(Enum.class);
                    splits[i] = field.getAnnotation(Split.class);
                    lengths[i] = field.getAnnotation(Length.class);
                    notNulls[i] = field.getAnnotation(NotNull.class);
                    substrings[i] = field.getAnnotation(Substring.class);
                    decimalMins[i] = field.getAnnotation(DecimalMin.class);
                    decimalMaxs[i] = field.getAnnotation(DecimalMax.class);
                    descriptions[i] = field.getAnnotation(Description.class);
                    isDuplicateds[i] = field.getAnnotation(IsDuplicated.class);

                    matchFlag[tempFieldIndex] = true;
                    break;
                }
                tempFieldIndex++;
            }
            Column column = new Column();
            column.setName(ExcelUtil.getValue(cell));
            if (descriptions[i] != null) {
                column.setDescription(descriptions[i].value());
            }
            if (types[i] != null) {
                column.setDataType(types[i].value());
            }
            if (notNulls[i] != null) {
                column.setRequired(true);
            }
            this.addColumn(column);
        }

		/* 如果文件不匹配 */
        if (columnMatchNumber != set.size()) {
            StringBuilder templateExcept = new StringBuilder();
            int tempIndex = 0;
            for (Field field : set) {
                if (matchFlag[tempIndex++]) {
                    continue;
                }
                templateExcept.append(field.getAnnotation(Name.class).value()).append("栏；");
            }
            throw new TemplateNotMatchException("不匹配的Excel文件，没有：" + templateExcept.toString());
        }

        int maxRowNumber = sheet.getLastRowNum();                   //Excel文件的总行数
        /* 逐行读取导入文件的数据 */
        for (int i = 0; i < maxRowNumber; i++) {
            Row inputRow = sheet.getRow(i + 1);                //Excel中的一行数据，第0行为表头，所以要加1
            DataRow row = new DataRow();                    //DataTable中的一行
            this.addRow(row);

            if (null != inputRow) {
                for (int j = 0; j < columnSum; j++) {                //逐格扫描

					/* 取得当前格子的值 */
                    org.apache.poi.ss.usermodel.Cell excelCell = inputRow.getCell(j);
                    Cell cell = new Cell();
                    this.setCell(i, columns[j].getName(), cell);

                    String value = "";
                    if (null != excelCell) {
                        value = ExcelUtil.getValue(excelCell);
                    }
                    value = value.trim();
                    boolean cellFlag = true;

					/* 如果要对字符进行切分 */
                    if (null != splits[j]) {
                        value = StringUtil.split(value, splits[j].reg(), splits[j].index());
                    }

					/* 如果要截取一段字符 */
                    if (null != substrings[j]) {
                        value = StringUtil.substring(value, substrings[j].start(), substrings[j].end());
                    }

                    cell.setValue(value);

					/* 如果要求非空 */
                    if (null != notNulls[j]) {
                        if (value.length() == 0) {
                            this.setStatus(i, j, Status.EMPTY);
                            cellFlag = false;
                        }
                    }

					/* 如果属于枚举类型 */
                    if (cellFlag && null != enums[j]) {
                        Class enumClass = enums[j].target();

                        if (!EnumUtil.check(enumClass, value)) {
                            this.setStatus(i, j, Status.ERROR_VALUE);
                            cellFlag = false;
                        }
                    }

					/* 如果对长度有要求 */
                    if (cellFlag && null != lengths[j]) {
                        if (value.length() > lengths[j].max() || value.length() < lengths[j].min()) {
                            this.setStatus(i, j, Status.LENGTH);
                            cellFlag = false;
                        }
                    }

					/* 如果对类型有要求 */
                    if (cellFlag && null != types[j] && !types[j].value().equals(DataType.STRING)) {
                        if (!DataType.check(types[j].value(), cell, value)) {
                            this.setStatus(i, j, Status.FORMAT);
                            cellFlag = false;
                        }
                    }

                    // 如果对最小值有要求
                    if (cellFlag && null != decimalMins[j] && value.length() > 0) {
                        Double dou = TransferUtil.transferDouble(value);
                        Double minValue = TransferUtil.transferDouble(decimalMins[j].value());
                        if (dou < minValue) {
                            this.setStatus(i, j, Status.TOO_SMALL);
                            cellFlag = false;
                        }
                    }

                    // 如果对最大值有要求
                    if (cellFlag && null != decimalMaxs[j] && value.length() > 0) {
                        Double dou = TransferUtil.transferDouble(value);
                        Double maxValue = TransferUtil.transferDouble(decimalMaxs[j].value());
                        if (dou > maxValue) {
                            this.setStatus(i, j, Status.TOO_BIG);
                            cellFlag = false;
                        }
                    }

					/* 如果不允许重复 */
                    if (cellFlag && null != isDuplicateds[j] && !isDuplicateds[j].value()) {
                        if (value.length() > 0) {
                            int tempSize = sets.get(j).size();
                            sets.get(j).add(value);
                            if (sets.get(j).size() == tempSize) {
                                this.setStatus(i, j, Status.REPEAT);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 获取所有的表头
     *
     * @return 表头数组
     */
    public Column[] getColumns() {
        return this.columns;
    }

    /**
     * 通过坐标取得单元格
     *
     * @param rowNumber  行坐标
     * @param columnName 列名
     * @return 对应坐标的单元格
     * @throws IndexOutOfBoundsException   下标越界
     * @throws ColumnNameNotExistException 列名不存在
     */
    public Cell getCell(int rowNumber, String columnName)
            throws IndexOutOfBoundsException, ColumnNameNotExistException {
        int columnNumber = getColumnIndex(columnName);
        return getCell(rowNumber, columnNumber);
    }

    /**
     * 通过坐标取得单元格
     *
     * @param rowNumber    行坐标
     * @param columnNumber 列坐标
     * @throws IndexOutOfBoundsException 下标越界
     */
    public final Cell getCell(int rowNumber, int columnNumber) throws IndexOutOfBoundsException {
        if (rowNumber > this.rowIndex || rowNumber < 0) {
            throw new IndexOutOfBoundsException("不存在的行坐标: " + rowNumber);
        }
        if (columnNumber > this.columnIndex || columnNumber < 0) {
            throw new IndexOutOfBoundsException("不存在的列坐标: " + columnNumber);
        }
        return data[rowNumber][columnNumber];
    }

    /**
     * 设置某个单元格的值。
     *
     * @param rowNumber    行坐标
     * @param columnNumber 列坐标
     * @param cell         单元格
     * @throws IndexOutOfBoundsException 坐标不存在的情况下，会报出异常，提示坐标不存在。
     */
    public void setCell(int rowNumber, int columnNumber, Cell cell) throws IndexOutOfBoundsException {
        if (rowNumber > this.rowIndex || rowNumber < 0) {
            throw new IndexOutOfBoundsException("不存在的行坐标: " + rowNumber);
        }
        if (columnNumber > this.columnIndex || columnNumber < 0) {
            throw new IndexOutOfBoundsException("不存在的列坐标: " + columnNumber);
        }
        data[rowNumber][columnNumber] = cell;
        this.setStatus(rowNumber, columnNumber, cell.getStatus());
    }

    /**
     * 设置某个单元格的值。注意：传入的是值
     *
     * @param rowIndex   行坐标
     * @param columnName 列坐标
     * @param cell       单元格
     * @throws IndexOutOfBoundsException   下标越界
     * @throws ColumnNameNotExistException 列名不存在
     */
    public void setCell(int rowIndex, String columnName, Cell cell)
            throws IndexOutOfBoundsException, ColumnNameNotExistException {
        int columnNumber = getColumnIndex(columnName);
        this.setCell(rowIndex, columnNumber, cell);
    }

    /**
     * 设置某个单元格的状态值 。注意：传入的是值
     *
     * @param rowNumber    行坐标
     * @param columnNumber 列坐标
     * @param status       状态值。
     * @throws IndexOutOfBoundsException 下标越界
     */
    public void setStatus(int rowNumber, int columnNumber, Status status) throws IndexOutOfBoundsException {
        if (!status.equals(Status.PASS)) {
            Cell cell = this.getCell(rowNumber, columnNumber);
            if (null == cell) {
                cell = new Cell();
                this.setCell(rowNumber, columnNumber, cell);
            }
            cell.setStatus(status);

            StringBuilder message = new StringBuilder(this.getColumns()[columnNumber].getName());
            message.append("栏");
            switch (status) {
                case NOTEXIST:
                    message.append("不存在的关联项；");
                    break;
                case EMPTY:
                    message.append("不能为空；");
                    break;
                case LENGTH:
                    message.append("长度超出限制；");
                    break;
                case EXIST:
                    message.append("已存在的记录；");
                    break;
                case ERROR_VALUE:
                    message.append("的值无法识别；");
                    break;
                case REPEAT:
                    message.append("重复的值；");
                    break;
                case TOO_SMALL:
                    message.append("过小的值");
                    break;
                case TOO_BIG:
                    message.append("过大的值");
                    break;
                case FORMAT:
                    message.append("格式错误；");
                    break;
            }
            this.appendError(rowNumber, message.toString());
        }
    }

    /**
     * 能过列名取得列下标。
     *
     * @param columnName 列名
     * @return 该列对应的下标
     * @throws ColumnNameNotExistException
     */
    private int getColumnIndex(String columnName) throws ColumnNameNotExistException {
        int columnIndex = -1;
        for (int i = 0; i < this.getColumnIndex(); i++) {
            if (this.getColumns()[i].getName().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            throw new ColumnNameNotExistException("不存在的列名：" + columnName);
        }
        return columnIndex;
    }

    /**
     * 设置某个单元格的值。注意：传入的是值
     *
     * @param rowNumber  行坐标
     * @param columnName 列坐标
     * @param status     单元格
     * @throws IndexOutOfBoundsException   下标越界
     * @throws ColumnNameNotExistException 列名不存在
     */
    public void setStatus(int rowNumber, String columnName, Status status)
            throws IndexOutOfBoundsException, ColumnNameNotExistException {
        int currentColumn = getColumnIndex(columnName);
        this.setStatus(rowNumber, currentColumn, status);
    }

    /**
     * 按行获取所有的单元格
     *
     * @return 行的数组
     */
    public DataRow[] getDataRows() {
        DataRow[] dataRows = new DataRow[this.rowIndex];
        for (int i = 0; i < this.rowIndex; i++) {
            DataRow row = new DataRow();
            for (int j = 0; j < this.columnIndex; j++) {
                row.put(this.columns[j].getName(), this.data[i][j]);
            }
            dataRows[i] = row;
        }
        return dataRows;
    }

    /**
     * 按列获取所有的单元格
     *
     * @return 获取数据列的Map。
     */
    public HashMap<String, List<Cell>> getDataColumns() {
        HashMap<String, List<Cell>> columns = new HashMap<>();
        for (int i = 0; i < this.columnIndex; i++) {
            List<Cell> list = new ArrayList<>();
            for (int j = 0; j < this.rowIndex; j++) {
                list.add(data[j][i]);
            }
            columns.put(this.columns[i].getName(), list);
        }
        return columns;
    }

    /**
     * 添加一行单元格。当数据量超过定义大小的一半时，会扩大容量至之前的1.5倍。
     *
     * @param row 一行记录
     */
    public void addRow(DataRow row) {
		/* 如果占用了一半以上，进行扩容 */
        if (this.rowIndex >= MAX_ROW_NUMBER / 2) {
            expandRow();
        }
        for (int i = 0; i < this.columnIndex; i++) {
            Cell cell = row.get(this.columns[i].getName());
            if (null != cell) {
                this.setCell(this.rowIndex, i, cell);
                int width = StringUtil.getByteLength(cell.getValue());
                if (width > this.getColumns()[i].getCellWidth()) {
                    if (width > 100) {
                        width = 100;
                    }
                    this.getColumns()[i].setCellWidth(width);
                }
            }
        }
        this.rowIndex++;
    }

    /**
     * 添加一列表头信息,会进行查重。当列数超过定义大小的一半时，会扩大容量至之前的1.5倍。
     *
     * @param column 要加入的表头信息
     */
    public void addColumn(Column column) {
		/* 如果占用了一半以上，进行扩容 */
        if (this.columnIndex >= MAX_COLUMN_NUMBER / 2) {
            expandColumn();
        }
        boolean exist = false;
        column.setName(column.getName().replace("*", ""));      //删除标题中的星号
        column.setName(column.getName().replace(" ", ""));       //删除标题中的空格
        for (int i = 0; i < this.columnIndex; i++) {
            if (column.getName().equals(this.getColumns()[i].getName())) {
                exist = true;
            }
        }
        if (!exist) {
            this.columns[this.columnIndex++] = column;
        } else {
            throw new ExistedColumnNameException("已存在名称为" + column.getName() + "的列");
        }
    }

    /**
     * 当表空间列数不足时，扩大表和表头的列数。
     */
    protected void expandColumn() {
        MAX_COLUMN_NUMBER *= (3 / 2) + 1;
		/* 扩展表头 */
        Column[] temp = new Column[MAX_COLUMN_NUMBER];
        System.arraycopy(columns, 0, temp, 0, columns.length);
        columns = temp;
        expand();
    }

    /**
     * 当表空间行数不足时，扩大表的行数。
     */
    protected void expandRow() {
        int begin = MAX_ROW_NUMBER;
        MAX_ROW_NUMBER *= (3 / 2) + 1;
        for (int i = 0; i < (MAX_ROW_NUMBER - begin); i++) {
            errorLists.add(new ArrayList<Error>());
        }
        expand();
    }

    /**
     * 在扩大表的大小后调用。<br/>
     * 会先把数据复制到临时数组中，再把临时数组指向data
     */
    protected void expand() {
        Cell[][] temp = new Cell[MAX_ROW_NUMBER][MAX_COLUMN_NUMBER];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, temp[i], 0, data[0].length);
        }
        data = temp;
    }

    /**
     * 转换某行为一个对象
     *
     * @param clazz    类型
     * @param rowIndex 行号
     * @return 对象
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public T transferOneObject(Class<T> clazz, int rowIndex)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        T object = clazz.newInstance();
        ParentFirst parentFirstAnnotation = clazz.getAnnotation(ParentFirst.class);
        boolean parentFirst = parentFirstAnnotation != null && parentFirstAnnotation.value();
        //Field[] fields = clazz.getDeclaredFields();
        Field[] fields = ClassUtil.getFields(clazz, parentFirst);
        Set<Field> set = new HashSet<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Name.class)) {
                set.add(field);
            }
        }

        for (int j = 0; j < this.getColumnIndex(); j++) {
            if (this.getColumns()[j].isHidden()) {
                continue;
            }

            String key = this.getColumns()[j].getName();
            if (key.equals(CHECK_STATUS_NAME) || key.equals(CHECK_STATUS_RESULT)) {
                continue;
            }

            for (Field field : set) {
                Name fieldName = field.getAnnotation(Name.class);

                if (key.equals(fieldName.value())) {

                    String att = StringUtil.upperFirstWord(field.getName());
                    String value;
                    Cell cell = this.getCell(rowIndex, j);
                    if (null != cell) {
                        value = cell.getValue();
                        Method method = clazz.getMethod("set" + att, field.getType());
                        if (field.isAnnotationPresent(Enum.class)) {
                            Enum e = field.getAnnotation(Enum.class);
                            String sValue = EnumUtil.getKey(e.target(), value);
                            if (field.getType() == String.class) {
                                method.invoke(object, sValue);
                            } else {
                                method.invoke(object, EnumUtil.valueOf(e.target(), sValue));
                            }
                        } else if (field.isAnnotationPresent(Type.class)) {
                            Type type = field.getAnnotation(Type.class);
                            switch (type.value()) {
                                case DATETIME:
                                case DATE: {
                                    Date date = TransferUtil.transferDate(value);
                                    method.invoke(object, date);
                                    break;
                                }
                                case DATEMINUTE: {
                                    Date date = TransferUtil.transferDateminute(value);
                                    method.invoke(object, date);
                                    break;
                                }
                                case DECIMAL:
                                    Double d = TransferUtil.transferDouble(value);
                                    method.invoke(object, d);
                                    break;

                                case NUMBER:
                                    Integer integer = TransferUtil.transferInteger(value);
                                    method.invoke(object, integer);
                                    break;

                                case BOOLEAN:
                                    Boolean b = TransferUtil.transferBoolean(value);
                                    method.invoke(object, b);
                                    break;

                                case LONG:
                                    Long l = TransferUtil.transferLong(value);
                                    method.invoke(object, l);
                                    break;
                                default:
                                    method.invoke(object, value);
                                    break;
                            }
                        } else {
                            method.invoke(object, value);
                        }
                    }
                    break;
                }
            }
        }
        return object;
    }

    /**
     * 将DataTable转为T型的List<br/><br/>
     * 如果你试图这么做,请确保在每个字段上都加上了@Name注解<br/>
     * 并确保该注解的值与DataTable中列的名字一致。<br/>
     * 注解的值在这里作为唯一的标识。
     *
     * @return T型列表
     * @see Column    列名称
     */
    public List<T> transferList(Class<T> clazz)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> list = new ArrayList<>();

        for (int i = 0; i < this.getRowIndex(); i++) {
            T object = transferOneObject(clazz, i);
            list.add(object);
        }
        return list;
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置表名
     *
     * @param name 表名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取当前总行数。
     *
     * @return 总行数
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * 获取当前总列数。
     *
     * @return 总列数
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    public final String toCSV() {
        StringBuilder sb = new StringBuilder();

        for (Column column : this.getColumns()) {
            if (column != null) {
                sb.append(column.getName()).append(",");
            }
        }
        sb.append("\n");

        for (int i = 0; i < this.getRowIndex(); i++) {
            for (int j = 0; j < this.getColumnIndex(); j++) {
                Cell cell = this.getCell(i, j);
                if (this.getColumns()[j].getDataType().equals(DataType.STRING)
                        || this.getColumns()[j].getDataType().equals(DataType.DATE)
                        || this.getColumns()[j].getDataType().equals(DataType.DATETIME)
                        || this.getColumns()[j].getDataType().equals(DataType.DATEMINUTE)) {
                    sb.append("\"\t").append(cell.getValue()).append("\",");
                } else {
                    sb.append(cell.getValue()).append(",");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public final String toString() {
        for (int i = 0; i < columnIndex; i++) {
            for (int j = 0; j < rowIndex; j++) {
                int length = StringUtil.getByteLength(this.getCell(j, i).getValue());
                if (columns[i].getCellWidth() < length) {
                    if (length > 100) {
                        length = 100;
                    }
                    columns[i].setCellWidth(length);
                }
            }
        }
        StringBuilder sb = new StringBuilder("");
        int sumWidth = 0;

		/* 打印表头 */

        for (int i = 0; i < this.getColumnIndex(); i++) {
            Column column = this.getColumns()[i];
            int width = column.getCellWidth() - StringUtil.getByteLength(column.getName()) + 4;
            int left = width / 2;
            int right = width - left;

            for (int j = 0; j < left; j++) {
                sb.append(" ");
            }
            sb.append(column.getName());
            for (int j = 0; j < right; j++) {
                sb.append(" ");
            }
            sumWidth += column.getCellWidth();
        }

        sb.append(CHECK_STATUS_NAME);
        sb.append(" ");
        sb.append(CHECK_STATUS_RESULT);
        sb.append("\n");
        for (int i = 0; i < sumWidth + (4 * this.getColumnIndex()) + 18; i++) {
            sb.append("=");
        }
        sb.append("\n");

		/* 打印数据 */
        for (int i = 0; i < this.getRowIndex(); i++) {
            for (int j = 0; j < this.getColumnIndex(); j++) {
                int cellWidth = this.getColumns()[j].getCellWidth();
                Cell cell = null;
                try {
                    cell = this.getCell(i, j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != cell) {
                    Object obj = cell.getValue();
                    int width = cellWidth - StringUtil.getByteLength(obj.toString()) + 4;
                    int left = width / 2;
                    int right = width - left;

                    for (int k = 0; k < left; k++) {
                        sb.append(" ");
                    }
                    sb.append(obj);
                    if (Status.PASS != cell.getStatus()) {
                        sb.append("*");
                        if (right > 1) {
                            right--;
                        }
                    }
                    for (int k = 0; k < right; k++) {
                        sb.append(" ");
                    }
                } else {
                    for (int k = 0; k < cellWidth + 4; k++) {
                        sb.append(" ");
                    }
                }
            }
            if (errorLists.get(i).size() == 0) {
                sb.append("  通过    ");
            } else {
                sb.append(" 不通过   ");
                for (Error error : errorLists.get(i)) {
                    sb.append(error.getMessage());
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 获取Table内所有的错误消息
     *
     * @return 所有的错误集合
     */
    public List<Error> getErrorList() {
        return errorList;
    }

    /**
     * 获取某一行的错误消息
     *
     * @param rowNumber 要获取错误消息的行数
     * @return 该行的错误消息
     * @throws IndexOutOfBoundsException 下标越界
     */
    public String getRowError(int rowNumber) throws IndexOutOfBoundsException {
        if (rowNumber > this.rowIndex) {
            throw new IndexOutOfBoundsException("不存在的行数：" + rowIndex);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (this.errorLists.get(rowNumber).size() > 0) {
            for (Error error : this.errorLists.get(rowNumber)) {
                stringBuilder.append(error.getMessage());
            }
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * 添加错误信息
     *
     * @param rowNumber 错误出现的行数
     * @param message   错误消息
     * @throws IndexOutOfBoundsException 下标越界
     */
    public void appendError(int rowNumber, String message) throws IndexOutOfBoundsException {
        if (rowNumber > this.rowIndex) {
            throw new IndexOutOfBoundsException("不存在的行数：" + rowNumber);
        }
        if (errorLists.get(rowNumber) == null) {
            errorLists.set(rowNumber, new ArrayList<Error>());
        }
        Error rowError = new Error(ErrorType.INVALID_PARAMETER, message);
        Error error = new Error(ErrorType.INVALID_PARAMETER, "第" + (rowNumber + 2) + "行：" + message);
        errorLists.get(rowNumber).add(rowError);
        errorList.add(error);
    }

    /**
     * 判断DataTable是否包含错误信息
     *
     * @return 是否包含错误.
     */
    public final boolean hasError() {
        return this.errorList.size() > 0;
    }

    @Deprecated
    public final String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        for (Error error : this.errorList) {
            sb.append(error.getMessage());
        }
        return sb.toString();
    }

    public List<ArrayList<Error>> getErrorLists() {
        return errorLists;
    }

    public void setErrorLists(List<ArrayList<Error>> errorLists) {
        this.errorLists = errorLists;
    }
}
