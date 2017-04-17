package com.wb.excel.api.datatable;

import com.wb.excel.api.annotation.Description;
import com.wb.excel.api.annotation.ExcelCollection;
import com.wb.excel.api.annotation.ExcelVerify;
import com.wb.excel.api.annotation.IsDuplicated;
import com.wb.excel.api.entity.*;
import com.wb.excel.api.style.ErrorCellStyle;
import com.wb.excel.api.util.ClassUtil;
import com.wb.excel.api.util.EnumUtil;
import com.wb.excel.api.util.StringUtils;
import com.wb.excel.api.util.VerifyDataUtil;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.wb.excel.api.annotation.Name;
import com.wb.excel.api.annotation.Enum;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:
 * ***************************************************************
 * </pre>
 */
public class ExcelImport {
    /**
     * 异常数据styler
     */
    private CellStyle errorMessageStyle;
    private boolean verfiyFail = false;
    private VerifyDataUtil verifyDataUtil = new VerifyDataUtil();
    private Map<Integer, DataVerifyResult> verifyResultMap = new HashMap<>();
    private CellStyle errorcellStyle;

    /**
     * 根据数据流解析Excel
     *
     * @param inputstream
     * @param pojoClass
     * @return
     */
    public ExcelImportResult importExcelByIs(InputStream inputstream, Class<?> pojoClass, ImportParams importParams) throws Exception {
        List<T> result = new ArrayList<T>();
        Workbook book = null;
        if (!(inputstream.markSupported())) {
            inputstream = new PushbackInputStream(inputstream, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(inputstream)) {
            book = new HSSFWorkbook(inputstream);
//            isXSSFWorkbook = false;
        } else if (POIXMLDocument.hasOOXMLHeader(inputstream)) {
            book = new XSSFWorkbook(OPCPackage.open(inputstream));
        }
//        createErrorCellStyle(book);
        errorcellStyle = new ErrorCellStyle(book).getStyle();
        for (int i = 0; i < importParams.getSheetNum(); i++) {
            result.addAll(importExcel(result, book.getSheetAt(i), pojoClass, importParams));
        }
        return new ExcelImportResult(result, verfiyFail, book, verifyResultMap);
    }

    /**
     * @param result
     * @param sheet
     * @param pojoClass
     * @param <T>
     * @return
     */
    private <T> Collection<? extends T> importExcel(List<T> result, Sheet sheet, Class<?> pojoClass, ImportParams importParams) throws Exception {
        List collection = new ArrayList();
        //Excel Field 对象
        Map<String, ExcelImportEntity> excelParams = new HashMap<String, ExcelImportEntity>();
        //Excel Line 对象
        List<ExcelCollectionEntity> excelCollection = new ArrayList<ExcelCollectionEntity>();
        if (!Map.class.equals(pojoClass)) {
            //获取所有的参数信息
            Field fields[] = ClassUtil.getClassFields(pojoClass);
            getAllExcelField(fields, excelParams, excelCollection, pojoClass, null);
        }
        Iterator<Row> rows = sheet.rowIterator();
        Map<Integer, String> titlemap = null;
        Row row = null;
        Object object = null;
        if (rows.hasNext()) {
            row = rows.next();// 排除标题信息
            titlemap = getTitleMap(row, excelParams, excelCollection);
        }
        while (rows.hasNext() && (row == null || sheet.getLastRowNum() - row.getRowNum() > 0)) {
            row = rows.next();//获取某一行信息
            // 判断是集合元素还是不是集合元素,如果是就继续加入这个集合,不是就创建新的对象
            if (isLineRow(row, excelParams, titlemap) && object != null) {
                for (ExcelCollectionEntity param : excelCollection) {
                    addListContinue(object, param, row, titlemap, importParams);
                }
            } else {
                object = ClassUtil.createObject(pojoClass);
                try {
                    for (int i = row.getFirstCellNum(), le = row.getLastCellNum(); i < le; i++) {
                        Cell cell = row.getCell(i) == null ? row.createCell(i) : row.getCell(i);
                        String titleString = (String) titlemap.get(i);
                        if (excelParams.containsKey(titleString) || Map.class.equals(pojoClass)) {
                            saveFieldValue(object, cell, excelParams, titleString, row, importParams);
                        }
                    }
                    for (ExcelCollectionEntity param : excelCollection) {
                        addListContinue(object, param, row, titlemap, importParams);
                    }
                    collection.add(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return collection;
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

    /**
     * 保存数据
     *
     * @param object
     * @param cell
     * @param excelParams
     * @param titleString
     * @param row
     */
    private void saveFieldValue(Object object, Cell cell, Map<String, ExcelImportEntity> excelParams, String titleString, Row row, ImportParams importParams) throws Exception {
        ExcelImportEntity entity = excelParams.get(titleString);
        String xclass = "class java.lang.Object";
        if (!(object instanceof Map)) {

            Method setMethod = entity.getMethods() != null && entity.getMethods().size() > 0 ? entity
                    .getMethods().get(entity.getMethods().size() - 1) : entity.getMethod();
            java.lang.reflect.Type[] ts = setMethod.getGenericParameterTypes();
            xclass = ts[0].toString();
        }
        Object result = getCellValue(xclass, cell, entity);

//        if (entity != null) {
        // 做值处理
//            result = replaceValue(entity., result);
//        }
        if (entity != null && entity.getEnum() != null) {
            boolean ischeck = EnumUtil.check(entity.getEnum(), String.valueOf(result));
            if (!ischeck) {
                DataVerifyResult verifyResult = new DataVerifyResult();
                verifyResult.setSuccess(false);
                verifyResult.setMsg("参数[" + entity.getShowname() + "," + result + "]未匹配相应的值信息");
                verifyResultMap.put(row.getRowNum(), verifyResult);
            } else {
                result = EnumUtil.getKey(entity.getEnum(), String.valueOf(result));
                setValues(entity, object, result);
            }
        }
        if (result instanceof Map) {
            ((Map) object).put(titleString, result);
        } else {
            //可以做值校验
            DataVerifyResult verifyResult = verifyDataUtil.verifyData(object, result, entity.getFiledName(), entity.getShowname(), entity.getVerify(), importParams.getVerifyHanlder());
            //设置校验结果(第一列)
            if (verifyResult.isSuccess()) {
                setValues(entity, object, result);
            } else {
                Integer rowNum = Integer.valueOf(row.getRowNum());
                if (verifyResultMap.containsKey(rowNum)) {
                    // 如果有错误信息，则添加错误数据
                    DataVerifyResult tempresult = verifyResultMap.get(rowNum);
                    tempresult.setMsg(tempresult.getMsg() + " " + verifyResult.getMsg());
                    verifyResultMap.put(rowNum, tempresult);
                } else {
                    verifyResultMap.put(rowNum, verifyResult);
                }
                verfiyFail = true;
            }
        }
    }

    public void setValues(ExcelImportEntity entity, Object object, Object value) throws Exception {
        if (entity.getMethods() != null) {
            setFieldBySomeMethod(entity.getMethods(), object, value);
        } else {
            /*
            if (String.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, value);
            } else if (Long.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Long.valueOf(value.toString()));
            } else if (int.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Integer.valueOf(value.toString()));
            } else if (boolean.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Boolean.valueOf(value.toString()));
            } else if (Boolean.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Boolean.valueOf(value.toString()));
            } else if (Double.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Double.valueOf(value.toString()));
            } else if (Integer.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Integer.valueOf(value.toString()));
            } else if (Long.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Long.valueOf(value.toString()));
            } else if (Float.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Float.valueOf(value.toString()));
            } else if (Short.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, Short.valueOf(value.toString()));
            } else if (Number.class.isAssignableFrom(entity.getMethod().getParameterTypes()[0])) {
                entity.getMethod().invoke(object, (Number) value);
            } else {

            }*/
            entity.getMethod().invoke(object, value);
        }
    }

    public void setFieldBySomeMethod(List<Method> setMethods, Object object, Object value)
            throws Exception {
        Object t = getFieldBySomeMethod(setMethods, object);
        setMethods.get(setMethods.size() - 1).invoke(t, value);
    }

    public Object getFieldBySomeMethod(List<Method> list, Object t) throws Exception {
        Method m;
        for (int i = 0; i < list.size() - 1; i++) {
            m = list.get(i);
            t = m.invoke(t, new Object[]{});
        }
        return t;
    }

    /**
     * 获取单元格内的值
     *
     * @param xclass
     * @param cell
     * @param entity
     * @return
     */
    private Object getCellValue(String xclass, Cell cell, ExcelImportEntity entity) {
        if (cell == null) {
            return "";
        }
        Object result = null;
        // 日期格式比较特殊,和cell格式不一致
        if ("class java.util.Date".equals(xclass) || ("class java.sql.Time").equals(xclass)) {
            if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                // 日期格式
                result = cell.getDateCellValue();
            } else {
                cell.setCellType(Cell.CELL_TYPE_STRING);
                result = getDateData(entity, cell.getStringCellValue());
            }
            if (("class java.sql.Time").equals(xclass)) {
                result = new Time(((Date) result).getTime());
            }
        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            result = cell.getNumericCellValue();
        } else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            result = cell.getBooleanCellValue();
        } else {
            result = cell.getStringCellValue();
        }
        if ("class java.lang.String".equals(xclass)) {
            result = String.valueOf(result);
        }
        return result;
    }

    /**
     * 获取日期类型数据
     *
     * @param entity
     * @param value
     * @return
     * @Author JueYue
     * @date 2013年11月26日
     */
    private Date getDateData(ExcelImportEntity entity, String value) {
        if (StringUtils.isNotEmpty(entity.getDateFormat()) && StringUtils.isNotEmpty(value)) {
            SimpleDateFormat format = new SimpleDateFormat(entity.getDateFormat());
            try {
                return format.parse(value);
            } catch (ParseException e) {
//                LOGGER.error("时间格式化失败,格式化:" + entity.getDateFormat() + ",值:" + value);
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 向List 里面添加元素
     *
     * @param object
     * @param param
     * @param row
     * @param titlemap
     */
    private void addListContinue(Object object, ExcelCollectionEntity param, Row row, Map<Integer, String> titlemap, ImportParams importParams) throws Exception {
        Collection collection = (Collection) ClassUtil.getMethod(param.getName(),
                object.getClass()).invoke(object, new Object[]{});
        Object entity = ClassUtil.createObject(param.getType());
        boolean isUsed = false;// 是否需要加上这个对象
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            String titleString = (String) titlemap.get(i);
            if (param.getExcelParams().containsKey(titleString)) {
                saveFieldValue(entity, cell, param.getExcelParams(), titleString, row, importParams);
                isUsed = true;
            }
        }
        if (isUsed) {
            collection.add(entity);
        }

    }

    /**
     * 查看某些列的数据，是否存在
     *
     * @param row
     * @param excelParams
     * @return
     */
    private Boolean isLineRow(Row row, Map<String, ExcelImportEntity> excelParams, Map<Integer, String> titlemap) {
        Boolean isLineRow = true;
        for (Integer index : titlemap.keySet()) {
            String titleName = titlemap.get(index);
            if (excelParams.containsKey(titleName)) {
                Cell cell = row.getCell(index);
                Object value = getCellValue("", cell, excelParams.get(titleName));
                if (!StringUtils.isEmpty(String.valueOf(value))) {
                    isLineRow = false;
                    break;
                }
            }
        }
        return isLineRow;
    }

    /**
     * 获取Title 与Index 关系
     *
     * @param row
     * @param excelParams
     * @param excelCollection
     * @return
     */
    private Map<Integer, String> getTitleMap(Row row, Map<String, ExcelImportEntity> excelParams, List<ExcelCollectionEntity> excelCollection) throws Exception {
        Map<Integer, String> titlemap = new HashMap<Integer, String>();
        Iterator<Cell> cellTitle = row.cellIterator();
        while (cellTitle.hasNext()) {
            Cell cell = cellTitle.next();
            String value = getKeyValue(cell).replace("*", "").replace("\n", "");
            int i = cell.getColumnIndex();
            //支持重名导入
            if (StringUtils.isNotEmpty(value)) {
                //判断当前列索引,是否为明细值
                if (!excelParams.containsKey(value)) {
                    if (excelCollection != null && !excelCollection.isEmpty()) {
                        for (ExcelCollectionEntity entity : excelCollection) {
                            if (entity.getExcelParams().containsKey(entity.getExcelName() + "_" + value)) {
                                ExcelImportEntity excelImportEntity = entity.getExcelParams().get(entity.getExcelName() + "_" + value);
                                excelImportEntity.setFiledName(entity.getName() + "_" + excelImportEntity.getFiledName());
                                titlemap.put(i, entity.getExcelName() + "_" + value);
                                break;
                            }
                        }
                    }
                } else {
                    titlemap.put(i, value);
                }
            }
        }
        //判断是否所有的列都满足，不满足抛Exception
        //1判断主要信息
        Set<String> params = excelParams.keySet();
        StringBuffer sb = new StringBuffer();
        for (String key : params) {
            if (!titlemap.containsValue(key)) {
                sb.append(key).append(" ");
            }
        }
        for (ExcelCollectionEntity entity : excelCollection) {
            Set<String> eparams = entity.getExcelParams().keySet();
            for (String key : eparams) {
                if (!titlemap.containsValue(key)) {
                    sb.append(key).append(" ");
                }
            }
        }
        if (sb.length() > 0) {
            throw new Exception("不匹配的Excel文件,缺少如下标题:" + sb.toString());
        }
        return titlemap;
    }

    /**
     * 获取key的值,针对不同类型获取不同的值
     *
     * @param cell
     * @return
     * @Author JueYue
     * @date 2013-11-21
     */
    private String getKeyValue(Cell cell) {
        Object obj = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                obj = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                obj = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                obj = cell.getNumericCellValue();
                break;
        }
        return obj == null ? null : obj.toString().trim();
    }

    /**
     * 解析获取Excel 信息
     *
     * @param fields
     * @param excelParams
     * @param excelCollection
     * @param pojoClass
     * @param getMethods
     */
    private void getAllExcelField(Field[] fields, Map<String, ExcelImportEntity> excelParams, List<ExcelCollectionEntity> excelCollection, Class<?> pojoClass, List<Method> getMethods) throws Exception {
        ExcelImportEntity excelEntity = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            // 判断是否有注解，没有注解，不需要解析
            Name name = field.getAnnotation(Name.class);
            ExcelCollection collectionannotation = field.getAnnotation(ExcelCollection.class);
            if (name != null || collectionannotation != null) {
                //如果是List 类型
                if (ClassUtil.isCollection(field.getType())) {
                    // 集合对象设置属性
                    ExcelCollectionEntity collection = new ExcelCollectionEntity();
                    collection.setName(field.getName());
                    Map<String, ExcelImportEntity> temp = new HashMap<String, ExcelImportEntity>();
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    Class<?> clz = (Class<?>) pt.getActualTypeArguments()[0];
                    collection.setType(clz);
                    getExcelFieldList(ClassUtil.getClassFields(clz), clz, temp, null);
                    collection.setExcelParams(temp);
                    collection.setExcelName(field.getAnnotation(ExcelCollection.class).value());
                    additionalCollectionName(collection);
                    excelCollection.add(collection);
                } else if (ClassUtil.isJavaClass(field)) {
                    addEntityToMap(field, excelEntity, pojoClass, getMethods, excelParams);
                } else {
                    List<Method> newMethods = new ArrayList<Method>();
                    if (getMethods != null) {
                        newMethods.addAll(getMethods);
                    }
                    newMethods.add(ClassUtil.getMethod(field.getName(), pojoClass));
                    getAllExcelField(ClassUtil.getClassFields(field.getType()),
                            excelParams, excelCollection, field.getType(), newMethods);
                }
            }
        }


    }

    private void addEntityToMap(Field field, ExcelImportEntity excelEntity, Class<?> pojoClass, List<Method> getMethods, Map<String, ExcelImportEntity> temp) throws Exception {
        Name excel = field.getAnnotation(Name.class);
        if (excel != null) {
            excelEntity = new ExcelImportEntity();
            excelEntity.setShowname(excel.value());
            excelEntity.setFiledName(field.getName());
            Enum targetEnum = field.getAnnotation(Enum.class);
            if (targetEnum != null) {
                excelEntity.setEnum(targetEnum.target());
            }
            excelEntity.setVerify(getImportVerify(field));
            Description description = field.getAnnotation(Description.class);
            excelEntity.setDescription(description == null ? "" : description.value());
            IsDuplicated isDuplicated = field.getAnnotation(IsDuplicated.class);
            if (isDuplicated != null) {
                excelEntity.setIsDuplicated(isDuplicated.value());
            }
            getExcelField(field, excelEntity, excel, pojoClass);
            if (getMethods != null) {
                List<Method> newMethods = new ArrayList<Method>();
                newMethods.addAll(getMethods);
                newMethods.add(excelEntity.getMethod());
                excelEntity.setMethods(newMethods);
            }
            temp.put(excelEntity.getShowname(), excelEntity);
        }
    }

    /**
     * 获取导入校验参数
     *
     * @param field
     * @return
     */
    public ExcelVerifyEntity getImportVerify(Field field) {
        ExcelVerify verify = field.getAnnotation(ExcelVerify.class);
        if (verify != null) {
            ExcelVerifyEntity entity = new ExcelVerifyEntity();
            entity.setEmail(verify.isEmail());
            entity.setInterHandler(verify.interHandler());
            entity.setMaxLength(verify.maxLength());
            entity.setMinLength(verify.minLength());
            entity.setMobile(verify.isMobile());
            entity.setNotNull(verify.notNull());
            entity.setRegex(verify.regex());
            entity.setRegexTip(verify.regexTip());
            entity.setTel(verify.isTel());
            return entity;
        }
        return null;
    }

    /**
     * 获取Excel 列信息
     *
     * @param field
     * @param excelEntity
     * @param excel
     * @param pojoClass
     */
    private void getExcelField(Field field, ExcelImportEntity excelEntity, Name excel, Class<?> pojoClass) throws Exception {
        String fieldname = field.getName();
        excelEntity.setMethod(ClassUtil.getMethod(fieldname, pojoClass, field.getType()));
        if (StringUtils.isNotEmpty(excel.dateFormat())) {
            excelEntity.setDateFormat(excel.dateFormat());
        }
    }

    private void additionalCollectionName(ExcelCollectionEntity collection) {
        Set<String> keys = new HashSet<String>();
        keys.addAll(collection.getExcelParams().keySet());
        for (String key : keys) {
            collection.getExcelParams().put(collection.getExcelName() + "_" + key,
                    collection.getExcelParams().get(key));
            collection.getExcelParams().remove(key);
        }
    }

    private void getExcelFieldList(Field[] fields, Class<?> pojoClass, Map<String, ExcelImportEntity> temp, List<Method> getMethods) throws Exception {
        ExcelImportEntity excelEntity = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (ClassUtil.isJavaClass(field)) {
                addEntityToMap(field, excelEntity, pojoClass, getMethods, temp);
            } else {
                List<Method> newMethods = new ArrayList<Method>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                newMethods.add(ClassUtil.getMethod(field.getName(), pojoClass, field.getType()));
                getExcelFieldList(ClassUtil.getClassFields(field.getType()),
                        field.getType(), temp, newMethods);
            }
        }
    }


}
