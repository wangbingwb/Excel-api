package com.wb.excel.api.util;

import com.wb.excel.api.entity.DataVerifyResult;
import com.wb.excel.api.entity.ExcelVerifyEntity;
import com.wb.excel.api.interfaces.IExcelVerifyHandler;

/**
 * ***************************************************************
 * <p/>
 * <pre>
 * Copyright (c) 2014 –
 *  Description:
 * ***************************************************************
 * </pre>
 */
public class VerifyDataUtil {
    private final static DataVerifyResult DEFAULT_RESULT = new DataVerifyResult(
            true);

    private void addVerifyResult(DataVerifyResult hanlderResult,
                                 DataVerifyResult result) {
        if (!hanlderResult.isSuccess()) {
            result.setSuccess(false);
            result.setMsg((StringUtils.isEmpty(result.getMsg()) ? "" : result.getMsg() + " , ")
                    + hanlderResult.getMsg());
        }
    }

    public DataVerifyResult verifyData(Object object, Object value, String name, String showName,
                                       ExcelVerifyEntity verify, IExcelVerifyHandler excelVerifyHandler) {
        if (verify == null) {
            return DEFAULT_RESULT;
        }
        DataVerifyResult result = new DataVerifyResult(true, "");
        if (verify.isNotNull()) {
            addVerifyResult(BaseVerifyUtil.notNull(showName, value), result);
        }
        if (verify.isEmail()) {
            addVerifyResult(BaseVerifyUtil.isEmail(showName, value), result);
        }
        if (verify.isMobile()) {
            addVerifyResult(BaseVerifyUtil.isMobile(showName, value), result);
        }
        if (verify.isTel()) {
            addVerifyResult(BaseVerifyUtil.isTel(showName, value), result);
        }
        if (verify.getMaxLength() != -1) {
            addVerifyResult(BaseVerifyUtil.maxLength(showName, value, verify.getMaxLength()), result);
        }
        if (verify.getMinLength() != -1) {
            addVerifyResult(BaseVerifyUtil.minLength(showName, value, verify.getMinLength()), result);
        }
        if (StringUtils.isNotEmpty(verify.getRegex())) {
            addVerifyResult(
                    BaseVerifyUtil.regex(showName, value, verify.getRegex(), verify.getRegexTip()),
                    result);
        }
        if (verify.isInterHandler()) {
            addVerifyResult(excelVerifyHandler.verifyHandler(object, name, value), result);
        }
        return result;

    }
}
