package common.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式化工具类
 *
 * @author zjl
 * @date 2019/5/21
 * @description:
 */
public class DateFormatUtil {
    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd
     */
    public static final int DEFAULT = 0;
    public static final int YM = 1;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd
     */
    public static final int YMR_SLASH = 11;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMdd
     */
    public static final int NO_SLASH = 2;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMM
     */
    public static final int YM_NO_SLASH = 3;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm:ss
     */
    public static final int DATE_TIME = 4;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd HH-mm-ss
     */
    public static final int DATE_TIME_SLASH = 13;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMddHHmmss
     */
    public static final int DATE_TIME_NO_SLASH = 5;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm
     */
    public static final int DATE_HM = 6;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm:ss
     */
    public static final int TIME = 7;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm
     */
    public static final int HM = 8;

    /**
     * 变量：日期格式化类型 - 格式:HHmmss
     */
    public static final int LONG_TIME = 9;
    /**
     * 变量：日期格式化类型 - 格式:HHmm
     */

    public static final int SHORT_TIME = 10;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd HH:mm:ss
     */
    public static final int DATE_TIME_LINE = 12;


    /**
     * 变量：日期格式化类型 - 格式yyyy.MM.dd
     */
    public static final int NO_SLASH_DOT = 14;

    public static String dateToStr(Date date, int type) {
        switch (type) {
            case DEFAULT:
                return dateToStr(date);
            case YM:
                return dateToStr(date, "yyyy/MM");
            case NO_SLASH:
                return dateToStr(date, "yyyyMMdd");
            case YMR_SLASH:
                return dateToStr(date, "yyyy-MM-dd");
            case YM_NO_SLASH:
                return dateToStr(date, "yyyyMM");
            case DATE_TIME:
                return dateToStr(date, "yyyy/MM/dd HH:mm:ss");
            case DATE_TIME_NO_SLASH:
                return dateToStr(date, "yyyyMMddHHmmss");
            case DATE_HM:
                return dateToStr(date, "yyyy/MM/dd HH:mm");
            case TIME:
                return dateToStr(date, "HH:mm:ss");
            case HM:
                return dateToStr(date, "HH:mm");
            case LONG_TIME:
                return dateToStr(date, "HHmmss");
            case SHORT_TIME:
                return dateToStr(date, "HHmm");
            case DATE_TIME_LINE:
                return dateToStr(date, "yyyy-MM-dd HH:mm:ss");
            case DATE_TIME_SLASH:
                return dateToStr(date, "yyyy-MM-dd HH-mm-ss");
            case NO_SLASH_DOT:
                return dateToStr(date, "yyyy.MM.dd");
            default:
                throw new IllegalArgumentException("Type undefined : " + type);
        }
    }

    /**
     * 将指定日期格式字符串转为date
     * @param str
     * @param type
     * @return
     */
    public static Date strToDate(String str, int type) {
        switch (type) {
            case DEFAULT:
                return strToDate(str);
            case YM:
                return strToDate(str, "yyyy/MM");
            case NO_SLASH:
                return strToDate(str, "yyyyMMdd");
            case YMR_SLASH:
                return strToDate(str, "yyyy-MM-dd");
            case YM_NO_SLASH:
                return strToDate(str, "yyyyMM");
            case DATE_TIME:
                return strToDate(str, "yyyy/MM/dd HH:mm:ss");
            case DATE_TIME_NO_SLASH:
                return strToDate(str, "yyyyMMddHHmmss");
            case DATE_HM:
                return strToDate(str, "yyyy/MM/dd HH:mm");
            case TIME:
                return strToDate(str, "HH:mm:ss");
            case HM:
                return strToDate(str, "HH:mm");
            case LONG_TIME:
                return strToDate(str, "HHmmss");
            case SHORT_TIME:
                return strToDate(str, "HHmm");
            case DATE_TIME_LINE:
                return strToDate(str, "yyyy-MM-dd HH:mm:ss");
            default:
                throw new IllegalArgumentException("Type undefined : " + type);
        }
    }

    public static Date strToDate(String str,String pattern){
        if (str.isEmpty()) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            Date dateStart = formatter.parse(str);
            return dateStart;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date strToDate(String str){
        return strToDate(str,"yyyy/MM/dd");
    }

    public static String dateToStr(Date date, String pattern) {
        if (date == null || date.equals("")) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, "yyyy/MM/dd");
    }



}
