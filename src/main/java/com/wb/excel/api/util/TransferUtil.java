package com.wb.excel.api.util;


import com.wb.excel.api.enumeration.YesNo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2014/9/27.
 *
 * @author
 * @version 0.1.0
 */
public class TransferUtil {
    public static Date transferDate(String value) {
        Date date = null;
        try {
            if (value.length() == 10) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.parse(value);
            } else if (value.length() > 10) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf.parse(value);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date transferDatetime(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date transferDateminute(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Integer transferInteger(String value) {
        try {
            Double d = Double.parseDouble(value);
            int i = d.intValue();
            if (d != i) {
                return null;
            } else {
                return i;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Double transferDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean transferBoolean(String value) {
        String key = EnumUtil.getKey(YesNo.class, value);
        if (key.equals("Y")) {
            return true;
        } else if (key.equals("N")) {
            return false;
        } else {
            return null;
        }
    }

    public static Long transferLong(String value) {
        try {
            Double d = Double.parseDouble(value);
            long i = d.longValue();
            if (d != i) {
                return null;
            } else {
                return i;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
