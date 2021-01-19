package common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description:
 * @author: zjl
 * @date: 2020-12-11 14:32
 **/
@Slf4j
public class TimeUtil {

    /**
     * 计算两个日期的天数差
     *
     * @param d1 减数
     * @param d2 被减数
     * @return
     */
    public static int dateSub(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        //long类型的日期也支持
        //cal1.setTimeInMillis(long);
        //cal2.setTimeInMillis(long);
        cal1.setTime(d1);
        cal2.setTime(d2);

        //获取日期在一年(月、星期)中的第多少天
        //第335天
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        //第365天
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        //获取当前日期所在的年份
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);

        //如果两个日期的是在同一年，则只需要计算两个日期在一年的天数差；
        //不在同一年，还要加上相差年数对应的天数，闰年有366天
        //不同年
        if (year1 != year2)
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                //闰年
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)
                {
                    timeDistance += 366;
                } else //不是闰年
                {
                    timeDistance += 365;
                }
            }
            log.info("" + timeDistance + (day2 - day1));
            return timeDistance + (day2 - day1);
        } else //同年
        {
            log.info("判断day2 - day1 : " + (day2 - day1));
            return day2 - day1;
        }
    }

    /**
     * 获取首期还款日
     * 规则
     * 根据固定还款日查询，当前日期日是否大于等于还款日的15天
     * 计算得出还款日在当月，还是下月
     *
     * @param repayDay         固定还款日
     * @param runningBatchTime 跑批时间 为空则取当前时间
     * @return
     */
    public static String getFirstRepayDate(int repayDay, String runningBatchTime) {
        //判断跑批
        Calendar calendar = Calendar.getInstance();
        if (StringUtils.isNotBlank(runningBatchTime)) {
            Date runningBatchDate = DateFormatUtil.strToDate(runningBatchTime, DateFormatUtil.NO_SLASH);
            calendar.setTime(runningBatchDate);
        }
        String dateStr = "";
        //计算当前日与还款日相差值
        int diff = repayDay - calendar.get(Calendar.DAY_OF_MONTH);
        //判断是否跨月
        int m = calendar.get(Calendar.MONDAY) + 1;
        if (diff < 0) {
            //跨月
            m += 1;
        }
        //判读是否跨年
        int y = calendar.get(Calendar.YEAR);
        if ((calendar.get(Calendar.MONDAY) + 1) == 12) {
            y = y + 1;
        }
        Date repayDate = DateFormatUtil.strToDate(y + "-" + m + "-" + repayDay, "yyyy-mm-dd");
        Date currnDate = calendar.getTime();
        diff = TimeUtil.dateSub(currnDate, repayDate);
        //判断跑批日期时间日是否大于固定还款日,并且两日相差大于等于15天
        if ((diff >= 15 || diff <= -15)) {
            //首期为当前月
            calendar.setTime(repayDate);
            dateStr = calendar.get(Calendar.YEAR) + "-"
                    + new DecimalFormat("00").format(calendar.get(Calendar.MONDAY) + 1) + "-"
                    + repayDay;
        } else {
            //首期为最近还款日期的下个月
            calendar.setTime(repayDate);
            calendar.add(Calendar.MONDAY, 1);
            dateStr = calendar.get(Calendar.YEAR) + "-"
                    + new DecimalFormat("00").format(calendar.get(Calendar.MONDAY) + 1) + "-"
                    + repayDay;
        }
        return dateStr;
    }

    public static void main(String[] args) {
        String s = TimeUtil.getFirstRepayDate(6, "20231225");
        System.out.println(s);
    }

}
