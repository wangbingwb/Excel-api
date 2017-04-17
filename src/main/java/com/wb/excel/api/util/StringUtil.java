package com.wb.excel.api.util;

import com.wb.excel.api.enumeration.YesNo;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DEV001 on 2014/9/1.
 */
public class StringUtil {

    public static int getByteLength(String str) {
        int length = str.replaceAll("[^\\x00-\\xff]", "**").length();
        return length;
    }

    public static String upperFirstWord(String str) {
        String temp = str.substring(0, 1);
        return temp.toUpperCase() + str.substring(1);
    }

    /**
     * TODO
     *
     * @param str
     * @param reg
     * @param index
     * @return
     */
    public static String split(String str, String reg, int index) {
        if (reg.length() == 0) {
            return str;
        }
        String[] strings = str.split(reg);
        if (index < 0) {
            index = 0;
        }
        if (index >= strings.length) {
            index = strings.length - 1;
        }
        return strings[index];
    }

    public static String substring(String str, int start, int end) {
        if (0 >= start || start >= str.length()) {
            end = str.length() - 1;
        }
        if (0 >= end || end >= str.length()) {
            end = str.length() - 1;
        }
        return str.substring(start, end);
    }

    public static String transferDate(String value) {
        Double d = TransferUtil.transferDouble(value);
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (d == null) {
            try {
                date = sdf.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            date = HSSFDateUtil.getJavaDate(d);
        }
        return sdf.format(date);
    }

    public static String transferDatetime(String value) {
        Double d = TransferUtil.transferDouble(value);
        Date date = null;

        List<String> sdfList = new ArrayList<>();
        sdfList.add("yyyy-MM-dd HH:mm:ss");
        sdfList.add("yyyy-MM-dd hh:mm:ss");
        sdfList.add("yyyy/MM/dd HH:mm:ss");
        sdfList.add("yyyy/MM/dd hh:mm:ss");

        SimpleDateFormat stdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (d == null) {
            for (String sdfValue : sdfList) {
                SimpleDateFormat sdf = new SimpleDateFormat(sdfValue);
                try {
                    date = sdf.parse(value);
                    break;
                } catch (ParseException ignored) {
                }
            }
        } else {
            date = HSSFDateUtil.getJavaDate(d);
        }
        return stdFormat.format(date);
    }

    public static String transferDateminute(String value) {
        Double d = TransferUtil.transferDouble(value);
        Date date = null;

        List<String> sdfList = new ArrayList<>();
        sdfList.add("yyyy-MM-dd HH:mm:ss");
        sdfList.add("yyyy-MM-dd hh:mm:ss");
        sdfList.add("yyyy/MM/dd HH:mm:ss");
        sdfList.add("yyyy/MM/dd hh:mm:ss");

        SimpleDateFormat stdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (d == null) {
            for (String sdfValue : sdfList) {
                SimpleDateFormat sdf = new SimpleDateFormat(sdfValue);
                try {
                    date = sdf.parse(value);
                    break;
                } catch (ParseException ignored) {
                }
            }
        } else {
            date = HSSFDateUtil.getJavaDate(d);
        }
        return stdFormat.format(date);
    }

    public static String transferInteger(String value) {
        try {
            Double d = Double.parseDouble(value);
            int i = d.intValue();
            if (d != i) {
                return null;
            } else {
                return "" + i;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String transferBoolean(String value) {
        String key = EnumUtil.getKey(YesNo.class, value);
        if (key.equals("Y")) {
            return "是";
        } else if (key.equals("N")) {
            return "否";
        }
        return "";
    }

    public static String transferLong(String value) {
        try {
            Double d = Double.parseDouble(value);
            long i = d.longValue();
            if (d != i) {
                return null;
            } else {
                return "" + i;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将字符串加密处理,例：sample@passionnetwork.com → samp***********.com
     *
     * @param input       想要加密的字符串
     * @param startLength 加密开始位置
     * @param endLength   加密结束位置
     * @param isAbsolute  是否保持原来长度
     * @return 加密后的字符串
     */
    public static String encryptString(String input, int startLength, int endLength, boolean isAbsolute) {
        int length = input.length();
        int endIndex = length - endLength;
        if (startLength > length || endLength > length) {
            return input;
        }

        String start = input.substring(0, startLength);
        String end = input.substring(endIndex);
        String out = start;

        if (isAbsolute) {
            for (int i = startLength; i < endIndex; i++) {
                out += "*";
            }
        } else {
            out += "****";
        }
        out += end;

        return out;
    }

    public static String fixNumberLength(int number, int length) {
        String fixedNumber = "" + String.valueOf(number);
        while (fixedNumber.length() < length) {
            fixedNumber = "0" + fixedNumber;
        }
        return fixedNumber;
    }
}
