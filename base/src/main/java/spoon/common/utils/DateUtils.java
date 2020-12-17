package spoon.common.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public abstract class DateUtils {

    private static final String DATE_PATTERN = "yyyy.MM.dd";

    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date parse(String date) {
        return parse(date, DATE_PATTERN);
    }

    public static Date parse(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            log.warn("날짜 형식으로 변환할 수 없습니다. 날짜 : {}, 형식 : {} - {}", date, pattern, e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            return null;
        }
    }

    public static int week() {
        return week(new Date());
    }

    public static int week(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1; // 실제는 1부터 시작 배열을 맞추기 위해서
    }

    public static Date beforeDays(int days) {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * days);
    }

    public static Date beforeDays(int days, Date date) {
        return new Date(date.getTime() - 1000 * 60 * 60 * 24 * days);
    }

    public static Date beforeHours(int hours) {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60 * hours);
    }

    public static Date beforeMinutes(int minutes) {
        return new Date(System.currentTimeMillis() - 1000 * 60 * minutes);
    }

    public static Date beforeSeconds(int seconds) {
        return new Date(System.currentTimeMillis() - 1000 * seconds);
    }

    public static String todayString() {
        return format(new Date());
    }

    /**
     * date query 시작시간 00:00:00.000
     */
    public static Date start(String date) {
        return DateUtils.parse(date);
    }

    /**
     * date query 종료시간 시작시간 + 1일 추가
     */
    public static Date end(String date) {
        return new Date(DateUtils.parse(date).getTime() + 1000 * 60 * 60 * 24 - 3); // ms-sql 3ms 범위
    }

    public static String sdate(String date) {
        return date.replaceAll("[^0-9]", "") + "%";
    }
}
