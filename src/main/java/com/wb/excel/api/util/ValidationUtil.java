package com.wb.excel.api.util;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类。提供一些通用简单的数据验证功能。
 * Created by DEV001 on 2014/8/1.
 */
public class ValidationUtil {

    private static Validator validator;

    /**
     * 检查ID是否合法
     *
     * @param id 要检查的ID
     * @return true, ID合法
     */
    public static Boolean checkId(Long id) {
        return !(null == id || id < 1);
    }

    public static Boolean checkDate(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(value);
                return true;
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static Boolean checkDatetime(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception e) {
            List<String> sdfList = new ArrayList<>();
            sdfList.add("yyyy-MM-dd HH:mm:ss");
            sdfList.add("yyyy-MM-dd hh:mm:ss");
            sdfList.add("yyyy/MM/dd HH:mm:ss");
            sdfList.add("yyyy/MM/dd hh:mm:ss");

            for (int i = 0; i < sdfList.size(); i++) {
                String sdfValue = sdfList.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat(sdfValue);
                try {
                    sdf.parse(value);
                    return true;
                } catch (ParseException ignored) {
                }
            }
            return false;
        }
        return true;
    }

    public static Boolean checkDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 检查一个字符串是否可以转为整数。
     *
     * @param value 要检查的字符串
     * @return true || false
     */
    public static Boolean checkInteger(String value) {
        try {
            Double d = Double.parseDouble(value);
            int i = d.intValue();
            return d == i;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查一个字符串是否可以转为长整数。
     *
     * @param value 要检查的字符串
     * @return true || false
     */
    public static boolean checkLong(String value) {
        try {
            Double d = Double.parseDouble(value);
            long l = d.longValue();
            return d == l;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查用户ID的列表是否合法
     *
     * @param ids 要检查的ID列表
     * @return true, 输入的ID列表合法
     */
    public static Boolean checkIdList(List<Long> ids) {
        return !(null == ids || ids.size() == 0);
    }

    /**
     * 检查字符串。不为空返回true；为空返回false。<br/>
     * <b>内容为空也认为是空</b>
     *
     * @param str 要检查的字符串
     * @return 检查的结果。true,检查通过；false,检查不通过。
     */
    public static Boolean checkString(String str) {
        return !(null == str || 0 == str.trim().length());
    }

    /**
     * 检查字符串的长度是否符合要求。<br><b>该方法不会删除字符串前后的空格</b>
     *
     * @param str       要进行判断的字符串
     * @param minLength 字符串限制的最小长度（包含）
     * @param maxLength 字符串限制的最大长度（包含）
     * @return 例如：限制长度 2，50。字符串为"aa"返回true，字符串为"a"返回false
     */
    public static Boolean checkStringLength(String str, int minLength, int maxLength) {
        if (null == str) {
            return false;
        } else {
            if (str.length() >= minLength && str.length() <= maxLength) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查邮箱格式是否正确<br/>
     *
     * @param email 要检查的邮箱地址
     * @return true, 格式正确；false，格式不正确<br/>
     * 例如：<br/>
     * 0123-abcd_ABCD@0123-abcd_ABCD.00aaBB--__.cc <b>true</b><br/>
     * a@a.a <b>true</b><br/>
     * tony@sina@qq.com <b>false</b>
     */
    public static Boolean checkEmail(String email) {
        //验证邮箱地址的正则表达式
        Pattern p;
        p = Pattern.compile(
                "^([a-zA-Z0-9]+[_|_|-|-|.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|_|-|-|.]?)*[a-zA-Z0-9]+.[a-zA-Z]{2,3}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 检查文件路径格式是否正确<br/>
     *
     * @param filePath 要检查的文件路径
     * @return true, 格式正确；false，格式不正确<br/>
     * 例如：<br/>
     * C:\b.txt <b>true</b><br/>
     * C:\abc\a.txt <b>true</b><br/>
     * C:\windows\sys<\mfc.dll <b>false</b>
     */
    public static Boolean checkFilePath(String filePath) {
        //验证文件路径格式的正则表达式
        Pattern p = Pattern.compile("^(?<path>(?:[a-zA-Z]:)?\\\\(?:[^\\\\\\?\\/\\*\\|<>:\"]+\\\\)+)(?<filename>(?<name>[^\\\\\\?\\/\\*\\|<>:\"]+?)\\.(?<ext>[^.\\\\\\?\\/\\*\\|<>:\"]+))$");
        Matcher m = p.matcher(filePath);
        return m.matches();
    }

    /**
     * 检查电话号码格式是否正确。
     *
     * @param phone 要检查的电话号码
     * @return true, 格式正确；false，格式不正确<br>
     * 例如：  <br>
     * 13345661234  <b>true</b> <br>
     * 16812341234  <b>false</b> <br>
     * 0512-123456  <b>false</b> <br>
     * 0512-2345678 <b>true</b> <br>
     * 12345678 <b>false</b> <br>
     * 22345678 <b>true</b>
     */
    public static Boolean checkPhone(String phone) {
        List<Pattern> list = new LinkedList<>();
        list.add(Pattern.compile("^[1][3,4,5,8][0-9]{9}$"));    //验证手机号
        list.add(Pattern.compile("^[0][1-9]{1,2}[0-9]-[2-9][0-9]{6,7}$"));  //验证带区号固定电话，区号后加“-”
        list.add(Pattern.compile("^[2-9][0-9]{6,7}$"));     //验证不带区号的固定电话
        list.add(Pattern.compile("([0][1-9]{1,2}[0-9])[2-9][0-9]{6,7}$"));

        Matcher matcher;
        Boolean result = false;
        for (Pattern pattern : list) {
            matcher = pattern.matcher(phone);   //遍历匹配
            result |= matcher.matches();        //对所有的匹配结果做或运算。
        }
        return result;
    }

    /**
     * 检查URL是否正确
     *
     * @param url
     * @return
     */
    public static Boolean checkUrl(String url) {
        List<Pattern> list = new LinkedList<>();
        list.add(Pattern.compile("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$"));

        Matcher matcher;
        Boolean result = false;
        for (Pattern pattern : list) {
            matcher = pattern.matcher(url);   //遍历匹配
            result |= matcher.matches();        //对所有的匹配结果做或运算。
        }
        return result;
    }

}
