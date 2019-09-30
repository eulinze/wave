/**
 * Copyright (c) 2008 CEPRI,Inc.All rights reserved.
 * Created by 2008-4-25
 */
package com.ylz.waveform.tools;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    //获取输入框十六进制格式
    public static String getHexString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f') ||
                    ('A' <= c && c <= 'F')) {
                sb.append(c);
            }
        }
        if ((sb.length() % 2) != 0) {
            sb.deleteCharAt(sb.length());
        }
        return sb.toString();
    }

    public static String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            sb.append(' ');
        }
        return sb.toString();
    }


    public static byte[] stringToBytes(String s) {
        byte[] buf = new byte[s.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            try {
                buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }

    public static String asciiToString(byte[] bytes) {
        char[] buf = new char[bytes.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char) bytes[i];
            sb.append(buf[i]);
        }
        return sb.toString();
    }


    /***************************************************************************
     * java字符串相应类型的处理
     **************************************************************************/
    // 过滤通过页面表单提交的字符
    private static String[][] FilterChars = {{"<", "&lt;"}, {">", "&gt;"},
            {" ", "&nbsp;"}, {"\"", "&quot;"}, {"&", "&amp;"},
            {"/", "&#47;"}, {"\\", "&#92;"}, {"\n", "<br>"}};
    // 过滤通过javascript脚本处理并提交的字符
    private static String[][] FilterScriptChars = {{"\n", "\'+\'\\n\'+\'"},
            {"\r", " "}, {"\\", "\'+\'\\\\\'+\'"},
            {"\'", "\'+\'\\\'\'+\'"}};

    /**
     * 手机号码表达式
     **/
    public static String PHONE_MACTH = "^(13[0-9]|14[0-9]|15[0|1|2|3|5|6|7|8|9]|17[0-9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

    public static String IDCARD_NUMBER = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";

    /**
     * @param oldStr    ：原字符串
     * @param strLen    ：返回字符串长度
     * @param padChar   ：插入字符串
     * @param direction ：插入方向
     * @return
     * @description: 补充字符的方法, 1、direction的取值为r(在原字符串右边补充)，l(在原字符串左边补充)
     */
    public static String padString(String oldStr, int strLen, char padChar,
                                   char direction) {
        String newStr = oldStr;
        try {
            if (oldStr.length() >= strLen) {
                newStr = oldStr;
            } else {
                if (direction == 'r') {
                    while (newStr.length() < strLen) {
                        newStr = newStr + padChar;
                    }
                } else {
                    while (newStr.length() < strLen) {
                        newStr = padChar + newStr;
                    }
                }
            }
            return newStr;
        } catch (Exception e) {
            return oldStr;
        }
    }


    public static int Str2int(String str, int defaultNum) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultNum;
        }

    }

    /**
     * 提供字符串到Vector的转变 *
     */
    public static Vector Str2Vect(String tStr, String sStr) {
        Vector vector = new Vector();
        StringTokenizer st = new StringTokenizer(tStr, sStr);
        while (st.hasMoreTokens()) {
            vector.add(st.nextToken());
        }
        return vector;
    }

    /**
     * 提供Vector到字符串的转变，转变后的字符串以sStr作为分割符 *
     */
    public static String Vect2Str(Vector tVect, String sStr) {
        String reStr = "";
        if (tVect.size() > 0)
            reStr = (String) tVect.get(0);
        for (int i = 1; i < tVect.size(); i++) {
            reStr += sStr + (String) tVect.get(i);
        }
        return reStr;
    }

    /**
     * 提供Vector到字符串的转变，转变后的字符串没有分割符 *
     */
    public static String Vect2Str(Vector tVect) {
        String reStr = "";
        for (int i = 0; i < tVect.size(); i++) {
            reStr += (String) tVect.get(i);
        }
        return reStr;
    }

    /**
     * 提供字符串到字符串数组的转变,转变后的字符串以sStr作为分割符 *
     */
    public static String[] Str2Strs(String tStr, String sStr) {
        StringTokenizer st = new StringTokenizer(tStr, sStr);
        String[] reStrs = new String[st.countTokens()];
        int n = 0;
        while (st.hasMoreTokens()) {
            reStrs[n] = st.nextToken();
            n++;
        }
        return reStrs;
    }

    /**
     * 将以separator分割的字符串str按cnt个拆分开
     *
     * @return String[]
     * @author Administrator 2009-12-3
     */
    public static String[] subStrToArray(String str, String separator, int cnt) {
        String[] arr = StringUtil.Str2Strs(str, separator);
        String[] ar = null;
        if (arr.length > cnt) {
            int num = (arr.length % cnt) > 0 ? 1 : 0;
            ar = new String[arr.length / cnt + num];
            int sta = 0;
            int end = (arr[0].length() + 1) * cnt - 1;
            for (int i = 0; i < ar.length; i++) {
                ar[i] = str.substring(sta, end);
                sta = end + 1;
                end = sta + (arr[0].length() + 1) * cnt - 1;
                if (sta > str.length()) {
                    break;
                }
                if (end > str.length()) {
                    end = str.length();
                    ar[i + 1] = str.substring(sta, end);
                    break;
                }
            }
        } else {
            ar = new String[1];
            ar[0] = str;
        }
        return ar;
    }

    /**
     * 提供字符串数组到字符串的转变，转变后的字符串以sStr作为分割符 *
     */
    public static String Strs2Str(String[] tStrs, String sStr) {
        String reStr = "";
        int len = tStrs.length;
        if (len > 0) {
            if (tStrs[0] != null)
                reStr = tStrs[0];
        }
        for (int i = 1; i < len; i++) {
            if (tStrs[i] != null) {
                if (tStrs[i].length() > 0)
                    reStr += sStr + tStrs[i];
            }
        }
        return reStr;
    }

    /**
     * 提供字符串数组到字符串的转变，转变后的字符串以sStr作为分割符,每个元素用''包含 *
     */
    public static String Strs2Str(String[] tStrs, String sStr, String tostr) {
        String reStr = "";
        int len = tStrs.length;
        if (len > 0) {
            if (tStrs[0] != null)
                reStr = "'" + tStrs[0] + "'";
        }
        for (int i = 1; i < len; i++) {
            if (tStrs[i] != null) {
                if (tStrs[i].length() > 0)
                    reStr += sStr + "'" + tStrs[i] + "'";
            }
        }
        return reStr;
    }

    public static double numberDecimal(double d, int i) {
        BigDecimal b = new BigDecimal(d);
        BigDecimal bd1 = b.setScale(i, b.ROUND_HALF_UP);
        d = bd1.doubleValue();
        return d;
    }

    /**
     * 字符串数组到字符串的转变，转变后的字符串没有分割符 *
     */
    public static String Strs2Str(String[] tStrs) {
        String reStr = "";
        int len = tStrs.length;
        for (int i = 0; i < len; i++) {
            if (tStrs[i] != null) {
                if (tStrs[i].length() > 0)
                    reStr += tStrs[i];
            }
        }
        return reStr;
    }

    /**
     * 字符串以指定长度进行切割，结果放入Vector对象中 *
     */
    public Vector Str2Vect(String tStr, int nleng) {
        int strLength = tStr.length();
        int ndiv = strLength / nleng;
        Vector reVect = new Vector();
        if (strLength % nleng == 0)
            ndiv--;
        for (int i = 0; i < (ndiv); i++) {
            reVect.add(tStr.substring(i * nleng, (i + 1) * nleng));
        }
        reVect.add(tStr.substring(ndiv * nleng, strLength));
        return reVect;
    }

    /**
     * 字符串相除，如果产生异常，返回"-" *
     */
    public static String Divide(String a, String b) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    / Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * 字符串相除 如果产生异常，返回re *
     */
    public static String Divide(String a, String b, String re) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    / Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return re;
        }
    }

    /**
     * 字符串相减，如果产生异常，返回re *
     */
    public static String decrease(String a, String b, String re) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    - Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return re;
        }
    }

    /**
     * 字符串相减，如果产生异常，返回a *
     */
    public static String decrease(String a, int b) {
        try {
            return String.valueOf(Integer.valueOf(a).intValue()
                    - Integer.valueOf(b).intValue());
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串相减，如果产生异常，返回a *
     */
    public static String decrease(String a, String b) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    - Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串减一 如果产生异常，返回a *
     */
    public static String decrease(String a) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue() - 1);
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串相加 如果产生异常，返回re *
     */
    public static String adding(String a, String b, String re) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    + Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return re;
        }
    }

    /**
     * 字符串相加 如果产生异常，返回a *
     */
    public static String adding(String a, String b) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    + Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串加一 如果产生异常，返回a *
     */
    public static String adding(String a) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue() + 1);
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串相乘 如果产生异常，返回re *
     */
    public static String multiply(String a, String b, String re) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    * Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return re;
        }
    }

    /**
     * 字符串相乘 如果产生异常，返回a *
     */
    public static String multiply(String a, String b) {
        try {
            return String.valueOf(Double.valueOf(a).doubleValue()
                    * Double.valueOf(b).doubleValue());
        } catch (Exception e) {
            return a;
        }
    }

    /**
     * 字符串(a-b)/b 如果产生异常，返回re *
     */
    public static String Tqb(String a, String b, String re) {
        try {
            return String.valueOf((Double.valueOf(a).doubleValue() - Double
                    .valueOf(b).doubleValue())
                    / (Double.valueOf(b).doubleValue()));
        } catch (Exception e) {
            return re;
        }
    }

    /**
     * 字符串(a-b)/b 如果产生异常，返回"-" *
     */
    public static String Tqb(String a, String b) {
        try {
            return String.valueOf((Double.valueOf(a).doubleValue() - Double
                    .valueOf(b).doubleValue())
                    / (Double.valueOf(b).doubleValue()));
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * 将String 替换操作，将str1替换为str2 *
     */
    public static String replace(String str, String str1, String str2) {
        int n = -1;
        String subStr = "";
        String re = "";
        if ((n = str.indexOf(str1)) > -1) {
            subStr = str.substring(n + str1.length(), str.length());
            re = str.substring(0, n) + str2 + replace(subStr, str1, str2);
        } else {
            re = str;
        }
        return re;
    }

    /**
     * 将字符串转换成Utf-8编码格式 *
     */
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串转换成GBK编码格式 *
     */
    public static String toGbkString(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("GBK");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    /**
     * 用给定的分隔符对字符串进行拆分，并生成数组
     *
     * @param message   需要拆分的字符串
     * @param separator 分隔符
     * @return 生成的数组
     */
    public static String[] splitToArray(String message, String separator) {
        List list = new ArrayList();
        int start = 0;
        int index = 0;
        while ((index = message.indexOf(separator, start)) != -1) {
            list.add(message.substring(start, index));
            start = index + separator.length();
        }

        if (start < message.length()) {
            list.add(message.substring(start, message.length()));
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 将字符串转换为java.sql.date类型,str的格式必须匹配给定的格式formatStr *
     */
    public static java.sql.Date str2SqlDate(String str, String formatStr) {
        java.sql.Date sqlDate = new java.sql.Date(0);// 默认获得当前时间
        try {
            sqlDate = new java.sql.Date(new SimpleDateFormat(
                    formatStr).parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sqlDate;
    }

    /**
     * 将对象的返回的值不能为空
     */
    public static String clear(Object obj) {
        if (null == obj || "null".equals(obj))
            return "";
        return obj.toString();
    }

    /**
     * 将对象的返回的值不能为空，否则返回0
     */
    public static String clearToZero(Object obj) {
        if (null == obj)
            return "0";
        return obj.toString();
    }

    /**
     * 若对象为空返回指定的值
     */
    public static String clear(Object obj, String value) {
        if (null == obj)
            return value;
        return obj.toString();
    }

    /**
     * 判断对象的值是否为空
     */
    public static boolean isNull(Object obj) {
        boolean flag = false;
        if (null == obj || "".equals(obj))
            flag = true;
        return flag;

    }

    /**
     * 判断对象的值是否为空
     */
    public static boolean isNotNull(Object obj) {
        boolean flag = true;
        if (null == obj || "".equals(obj))
            flag = false;
        return flag;

    }


    public static boolean isNotNull(List l) {
        if (null != l && l.size() > 0)
            return true;
        return false;
    }

    public static boolean isNotNull(String str) {
        if (null != str && str.trim().length() > 0)
            return true;
        return false;
    }

    public static boolean isNotNull(String[] str) {
        if (null != str && str.length > 0)
            return true;
        return false;
    }

    public static String replaceStrNullOrNot(String str) {
        if (null != str && str.length() > 0) {
            return str;
        } else {
            return " ";
        }
    }

    public static String replaceStrNullOrNot(Object obj) {
        if (null != obj && !"".equals(obj)) {
            return obj.toString();
        } else {
            return " ";
        }
    }

    /**
     * 方法名称:isNull
     * <p>
     * 方法描述:判断是字符串是否为空
     * <p>
     * 参数:
     *
     * @param str 字符串 参数:
     * @return boolean
     * <p>
     * <p>
     * @author HJun
     * <p>
     * @date Sep 2, 2009
     * <p>
     */
    public static boolean isNull(String str) {
        if (null == str || "".equals(str.trim()))
            return true;
        return false;
    }

    /**
     * 方法名称:isNull
     * <p>
     * 方法描述: 判断结果集是否为空
     * <p>
     * 参数:
     *
     * @param list 结果集对象 参数:
     * @return boolean
     * <p>
     * <p>
     * @author HJun
     * <p>
     * @date Sep 2, 2009
     * <p>
     */
    public static boolean isNull(List list) {
        if (null == list || list.size() == 0)
            return true;
        return false;
    }

    /**
     * 方法名称:isNull
     * <p>
     * 方法描述: 判断字符串数组是否为空
     * <p>
     * 参数:
     *
     * @param str String[] 字符串数组 参数:
     * @return boolean
     * <p>
     * <p>
     * @author HJun
     * <p>
     * @date Sep 2, 2009
     * <p>
     */
    public static boolean isNull(String[] str) {
        if (null == str || str.length == 0)
            return true;
        return false;
    }

    public static String translateToChinese(String str) {
        if (str != null && !"".equals(str) && IsNumber(str)) {
            return translateToChinese(Integer.parseInt(str));
        } else {
            return "0";
        }

    }

    /**
     * @param a 原始数字
     * @return 中文字符串
     * @方法名 translate
     * @功能 简单的数字转中文
     */
    public static String translateToChinese(int a) {

        String[] units = {"", "十", "百", "千", "万", "十", "百", "千", "亿"};
        String[] nums = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

        String result = "";
        if (a < 0) {
            result = "负";
            a = Math.abs(a);
        }
        String t = String.valueOf(a);
        for (int i = t.length() - 1; i >= 0; i--) {
            int r = (int) (a / Math.pow(10, i));
            if (r % 10 != 0) {
                String s = String.valueOf(r);
                String l = s.substring(s.length() - 1, s.length());
                result += nums[Integer.parseInt(l) - 1];
                result += (units[i]);
            } else {
                if (!result.endsWith("零")) {
                    result += "零";
                }
            }
        }
        String num = a + "";
        /*
         * 因为方法对10-20之间的数字支持不好，比如11返回一十一，不能满足需求 所以这里单独判断
         */
        if (a == 10) {
            return "十";
        } else if (a > 10 && a < 20) {
            return result.substring(1);
        } else if (num.endsWith("0")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static boolean eq(String str, Object o) {
        if (null != o && isNotNull(o)) {
            return o.equals(str);
        }
        return false;
    }

    public static boolean eq(Object o1, Object o2) {
        if (null == o1 || isNull(o1)) {
            if (o2 == null || isNull(o2)) {
                return true;
            } else {
                return false;
            }
        } else {
            return o1.equals(o2);
        }
    }

    /**
     * @return String
     * @author Administrator 2009-9-20
     */
    public static String toString(List list) {
        StringBuffer reStr = new StringBuffer("");
        for (Object o : list) {
            reStr.append(o.toString());
            reStr.append(",");
        }
        return reStr.toString();
    }

    /**
     * 截取字符串
     *
     * @param src 待截取字符串
     * @param num 截取长度
     * @return 截取后的字符串
     */
    public static String cutString(String src, int num) {
        if (isNull(src))
            return src;
        return src.substring(0, src.length() > num ? num : src.length());
    }

    public static double mulite(String str, String str2) {
        double d1 = Double.parseDouble(str);
        double d2 = Double.parseDouble(str2);
        return d1 * d2;
    }

    /**
     * @param str ：true表示是，false表示否
     * @函数名称：IsNumber
     * @功能描述：是否数字
     * @return：是或否
     * @exception: null 空指针异常
     */
    public static boolean IsNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断电话号码
     *
     * @param mobiles 电话号
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile(PHONE_MACTH);
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    /**
     * 判断是否为身份证号码
     *
     * @param IDCard
     * @return
     */
    public static boolean isIDCardNum(String IDCard) {
        Pattern p = Pattern.compile(IDCARD_NUMBER);
        Matcher m = p.matcher(IDCard);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    /**
     * @param str ：true表示是，false表示否
     * @函数名称：IsNumber
     * @功能描述：是数值
     * @return：是或否
     * @exception: null 空指针异常
     */
    public static boolean IsFloat(String str) {
        Pattern pattern = Pattern.compile("\\d+[.]?\\d*");
        return pattern.matcher(str).matches();
    }

    /**
     * XML字符串中不能用这些字符 < > ' " &
     *
     * @param str 待转换的字符串
     * @return String 转换后的字符串
     * @author zhangg
     */
    public static String replace4XML(String str) {
        if (str == null)
            return "";
        return str.replace("<", "&lt;").replace(">", "&gt;")
                .replace("'", "&apos;").replace("\"", "&quot;")
                .replace("&", "&amp;");
    }

    /**
     * 和“WebContent/skins/default/js/sotowerfunction.js”里的方法“tag2code()”是一对，
     * 需要一起修改
     *
     * @函数名称：HtmlTag2String
     * @功能描述：转换html符号为字符
     * @param：String
     * @return：String
     */
    public static String HtmlTag2String(String temp) {
        temp = temp == null ? "" : temp;
        return temp.replace("-lt;", "<").replace("-gt;", ">")
                .replace("-amp;", "&");
    }

    /**
     * 方法名称：zero 方法描述：设置小数，前面没有0增加0
     *
     * @param str
     * @return String
     * @author HJun
     * @version 1.0 Dec 17, 2009
     */
    public static String zero(String str) {
        if (str.startsWith(".")) {
            str = "0".concat(str);
        }
        return str;
    }

    /**
     * @函数名称：String2HtmlTag
     * @功能描述：转换“<”、“>”为html符号
     * @param：String
     * @return：String
     */
    public static String String2HtmlTag(String temp) {
        temp = temp == null ? "" : temp;
        return temp.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&acute;");
    }

    /**
     * split("separator",'a') returns {"sep","r","tor"}
     *
     * @param src
     * @return <br>
     * @函数名称：convertToUTF8
     * @功能描述：方法描述拆分字符串
     */
    public static String[] split(String src, char separator) {

        if (src == null)
            return null;
        else
            src = src.trim();
        int sprtCount = count(src, separator);
        if (sprtCount == 0) {
            String[] det = new String[1];
            det[0] = src;
            return det;
        }
        String[] det = new String[sprtCount + 1];
        int indexs = 0, indexe = 0;
        for (int i = 0; i <= sprtCount; i++) {
            indexe = src.indexOf(separator, indexs);
            if (indexe == -1)
                det[i] = src.substring(indexs);
            else {
                det[i] = src.substring(indexs, indexe);
                indexs = indexe + 1;
            }
        }
        return det;
    }

    /**
     * @函数名称：count
     * @功能描述：方法描述个数
     */
    public static int count(String ptr, char c) {
        int coun = 0, pos = 0;
        while ((pos = ptr.indexOf(c, pos)) != -1) {
            coun++;
            pos++;
        }
        return coun;
    }

    /**
     * @param c    String
     * @return String
     * @函数名称：count
     * @功能描述：方法描述个数
     */
    public static int count(String ptr, String c) {
        int coun = 0, pos = 0;
        while ((pos = ptr.indexOf(c, pos)) != -1) {
            coun++;
            pos += c.length();
        }
        return coun;
    }

    /**
     * @param obj String
     * @param x   double
     * @return double
     * @函数名称：getDoubleXDigit
     * @功能描述：保留X位小数
     */
    public static double getDoubleXDigit(String obj, double x) {
        double tmpD = Double.parseDouble(obj);
        double y = 10;
        y = Math.pow(y, x);
        tmpD = tmpD * y;
        tmpD = Math.round(tmpD);
        tmpD = tmpD / y;
        return tmpD;
    }

    public static String getReplaceStr(String str, String regStr) {
        Pattern p = Pattern.compile(regStr);
        Matcher m = p.matcher(str);
        str = m.replaceAll("");
        return str;
    }

    public static String getBackName(String fileName) {
        if (isNull(fileName))
            return "";
        if (fileName.lastIndexOf(".") < 0)
            return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getGBK(String str, String code) {
        try {
            return new String(str.getBytes(), code);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 提供字符串到ArrayList的转变 *
     */
    public static List<String> str2List(String tStr, String sStr) {
        if (isNull(tStr)) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(tStr, sStr);
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    /**
     * 方法名称： 方法描述：
     *
     * @version 1.0
     */
    public static List<Map<String, String>> compareObj(Object obj1, Object obj2)
            throws Exception {
        Class c = obj1.getClass();
        Field fields[] = c.getDeclaredFields();
        List<Map<String, String>> clist = new ArrayList<Map<String, String>>();

        for (Field field : fields) {
            field.setAccessible(true);
            Object val1 = invokeMethod(obj1, field.getName(), null);
            Object val2 = invokeMethod(obj2, field.getName(), null);
            System.out.println(field.getName() + " : val1=" + val1
                    + "---割割割割----val2=" + val2);
            if (val1 != null && val2 != null) {
                if (!val1.equals(val2)) {
                    Map<String, String> ma = new HashMap<String, String>();
                    ma.put("beforValue", val1.toString());
                    ma.put("afterValue", val2.toString());
                    ma.put("colomnCode", convertString(field.getName()));
                    // ma.put("tableCode", c.getName());
                    clist.add(ma);
                }

            }
            // else if(val1==null){
            // if(val2!=null){
            // // return true;
            // }else{
            //
            // }
            // }
            //

        }
        return clist;
    }

    /**
     * 方法名称： 方法描述：
     *
     * @version 1.0
     */
    public static Object invokeMethod(Object owner, String methodName,
                                      Object[] args) throws Exception {
        Class ownerClass = owner.getClass();
        System.out.println("Method=" + methodName);
        methodName = methodName.substring(0, 1).toUpperCase()
                + methodName.substring(1);
        Method method = null;
        try {
            method = ownerClass.getMethod("get" + methodName);

        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
            return " can't find 'get" + methodName + "' method";
        }

        return method.invoke(owner);
    }

    /**
     * 方法名称： 方法描述：
     *
     * @version 1.0
     */
    public static String convertString(String s) {
        if (isNotNull(s)) {
            char[] c = s.toCharArray();
            String str = "";
            int i = 0;
            for (char ch : c) {
                if (Character.isUpperCase(ch) && i > 0) {
                    str += "_";
                }
                str += ch;
                i++;
            }
            return str;
        } else {
            return "";
        }
    }

    public static String convertString1(String s) {
        if (isNotNull(s)) {
            String ss = s.toLowerCase();
            char[] carr = ss.toCharArray();
            String str = "";
            char temp = 0;
            for (int i = 0; i < carr.length; i++) {
                if (i > 0)
                    temp = carr[i - 1];
                if (Character.isLetter(carr[i]) && temp == '_') {
                    str += (carr[i] + "").toUpperCase();
                } else {
                    str += carr[i];
                }
            }
            str = str.replace("_", "");
            return str;
        } else {
            return "";
        }
    }

    /**
     * @author zhangling 在模糊查询中过滤容易引发SQL语句执行异常的符号
     */
    public static String ReplaceSqlLike(String strQuery) {
        String strRet = strQuery;
        strRet = strRet.replace("/", "//");
        strRet = strRet.replace("'", "''");
        strRet = strRet.replace("%", "/%");
        strRet = strRet.replace("[", "/[");
        strRet = "'%" + strRet + "%' escape '/'";
        return strRet;
    }

    /**
     * @return返回时间格式字符串。按年，月
     */
    public static String getDirName() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        return df.format(date);
    }

    /**
     * 用特殊的字符连接字符串
     *
     * @param strings     要连接的字符串数组
     * @param spilit_sign 连接字符
     * @return 连接字符串
     */
    public static String stringConnect(String[] strings, String spilit_sign) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            str.append(strings[i]);
            str.append(spilit_sign);
        }
        return str.toString();
    }

    /**
     * 过滤字符串里的的特殊字符
     *
     * @param str 要过滤的字符串
     * @return 过滤后的字符串
     */
    public static String stringFilter(String str) {
        String[] str_arr = stringSpilit(str, "");
        for (int i = 0; i < str_arr.length; i++) {
            for (int j = 0; j < FilterChars.length; j++) {
                if (FilterChars[j][0].equals(str_arr[i]))
                    str_arr[i] = FilterChars[j][1];
            }
        }
        return (stringConnect(str_arr, "")).trim();
    }

    /**
     * 过滤脚本中的特殊字符（包括回车符(\n)和换行符(\r)）
     *
     * @param str 要进行过滤的字符串
     * @return 过滤后的字符串
     */
    public static String stringFilterScriptChar(String str) {
        String[] str_arr = stringSpilit(str, "");
        for (int i = 0; i < str_arr.length; i++) {
            for (int j = 0; j < FilterScriptChars.length; j++) {
                if (FilterScriptChars[j][0].equals(str_arr[i]))
                    str_arr[i] = FilterScriptChars[j][1];
            }
        }
        return (stringConnect(str_arr, "")).trim();
    }

    /**
     * 分割字符串
     *
     * @param str         要分割的字符串
     * @param spilit_sign 字符串的分割标志
     * @return 分割后得到的字符串数组
     */
    public static String[] stringSpilit(String str, String spilit_sign) {
        String[] spilit_string = str.split(spilit_sign);
        if (spilit_string[0].equals("")) {
            String[] new_string = new String[spilit_string.length - 1];
            for (int i = 1; i < spilit_string.length; i++)
                new_string[i - 1] = spilit_string[i];
            return new_string;
        } else
            return spilit_string;
    }

    /**
     * 字符串字符集转换
     *
     * @param str 要转换的字符串
     * @return 转换过的字符串
     */
    public static String stringTransCharset(String str) {
        String new_str = null;
        try {
            new_str = new String(str.getBytes("iso-8859-1"), "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_str;
    }

    /**
     * @param @param  rateingString 实际评价字符串
     * @param @param  defaultString 数据异常时展示的字符串
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: stringToRateString
     * @Description: TODO(将评价字符串转换为小数点后一位的字符串)
     */
    public static String stringToRateString(String rateingString, String defaultString) {
        if (IsNumber(rateingString)) {
            return rateingString;
        } else if (IsFloat(rateingString)) {
            try {
                DecimalFormat df = new DecimalFormat("0.0");
                double x = Double.parseDouble(rateingString);
                return df.format(x);
            } catch (Exception e) {
                return defaultString;
            }
        } else {
            return defaultString;
        }
    }

    /**
     * 转为价格（小数点后保留两位小数）
     *
     * @param rateingString
     * @param defaultString
     * @return
     */
    public static String stringToPriceString(String rateingString, String defaultString) {
        if (IsNumber(rateingString)) {
            return rateingString;
        } else if (IsFloat(rateingString)) {
            try {
                DecimalFormat df = new DecimalFormat("0.00");
                double x = Double.parseDouble(rateingString);
                return df.format(x);
            } catch (Exception e) {
                return defaultString;
            }
        } else {
            return defaultString;
        }
    }

    public static String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isOtherDay(String compareTime, String todayEndTime) {
        Calendar compareTimeCalendar = Calendar.getInstance();
        Calendar todayEndTimeCalendar = Calendar.getInstance();
        try {
            compareTimeCalendar.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(compareTime));
            todayEndTimeCalendar.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(todayEndTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return compareTimeCalendar.getTimeInMillis() > todayEndTimeCalendar.getTimeInMillis();
    }

    public static String inputStreamToStr(InputStream in) {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        try {
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

}
