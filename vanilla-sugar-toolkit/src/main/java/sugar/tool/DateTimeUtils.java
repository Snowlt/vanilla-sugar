package sugar.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * 日期时间处理工具
 * <p>主要针对 {@link LocalDateTime}, {@link ZonedDateTime}, {@link Date} 对象和时间戳补充了一些方法，简化转换。
 * 其中的大部分方法支持在参数传入 {@code null} 时尽量不抛出 {@link NullPointerException}。</p>
 *
 * @author SnowLT
 * @version 1.0
 * @implNote 由于 Java 8 新增的日期/时间对象（{@link Temporal} 的实现类: {@link LocalDateTime} 等）原生提供了大量且便捷的方法，
 * 故这个工具类中没有做过多整合了。
 */
public class DateTimeUtils {

    private static final ZoneId ZERO_ZONE_ID = ZoneId.of("UTC+0");

    /**
     * 获取当前时间的 Unix 时间戳(以秒为单位)
     * <p>备注：一般说的 Unix 时间戳使用的是以秒为单位，如果需精确到毫秒可用 {@link #milliTimestamp()} 获取</p>
     *
     * @return 时间戳(秒级)
     * @see #milliTimestamp()
     */
    public static long unixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间的时间戳(以毫秒为单位)
     *
     * @return 时间戳(毫秒级)
     * @see #unixTimestamp()
     */
    public static long milliTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 将 Unix 时间戳(以秒为单位)转换为 {@link LocalDateTime} 对象
     * <p>返回的时间总是 UTC 0 时区(UTC+0000)的时间
     *
     * @param unixTimestamp 时间戳(以秒为单位)
     * @return 表示相同时间的 {@link LocalDateTime} 对象
     */
    public static LocalDateTime unixTimestampToDateTime(long unixTimestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp), ZERO_ZONE_ID);
    }

    /**
     * 将时间戳(以毫秒为单位)转换为 {@link LocalDateTime} 对象
     * <p>返回的时间总是 UTC 0 时区(UTC+0000)的时间
     *
     * @param milliTimestamp 时间戳(以毫秒为单位)
     * @return 表示相同时间的 {@link LocalDateTime} 对象
     */
    public static LocalDateTime milliTimestampToDateTime(long milliTimestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliTimestamp), ZERO_ZONE_ID);
    }

    /**
     * 将日期时间字符串解析为 {@link Date} 对象
     *
     * @param date    日期时间字符串
     * @param pattern 格式
     * @return {@link Date} 对象
     * @throws DateTimeException 如果解析日期失败或格式不正确则抛出
     */
    public static Date parseToDate(String date, String pattern) {
        if (date == null) return null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(date);
        } catch (ParseException | IllegalArgumentException | NullPointerException e) {
            throw new DateTimeException(e.getMessage(), e);
        }
    }

    /**
     * 将日期时间字符串解析为 {@link LocalDateTime} 对象
     *
     * @param dateTime 日期时间字符串
     * @param pattern  格式
     * @return {@link LocalDateTime} 对象
     * @throws DateTimeException 如果解析日期失败或格式不正确则抛出
     */
    public static LocalDateTime parseToLocalDateTime(String dateTime, String pattern) {
        if (dateTime == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTime, formatter);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DateTimeException(e.getMessage(), e);
        }
    }

    /**
     * 将日期时间字符串解析为 {@link ZonedDateTime} 对象
     *
     * @param dateTime 日期时间字符串
     * @param pattern  格式
     * @return {@link ZonedDateTime} 对象
     * @throws DateTimeException 如果解析日期失败或格式不正确则抛出
     */
    public static ZonedDateTime parseToZonedDateTime(String dateTime, String pattern) {
        if (dateTime == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return ZonedDateTime.parse(dateTime, formatter);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DateTimeException(e.getMessage(), e);
        }
    }

    /**
     * 将 {@link Date} 对象按指定的格式转换为字符串
     *
     * @param date    日期时间字符串
     * @param pattern 格式
     * @return 格式化后的字符串
     * @throws DateTimeException 如果格式不正确则抛出
     */
    public static String format(Date date, String pattern) {
        if (date == null) return null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DateTimeException(e.getMessage(), e);
        }
    }

    /**
     * 将日期/时间对象（例如: {@link LocalDateTime}, {@link ZonedDateTime}）按指定的格式转换为字符串
     *
     * @param dateTime 日期/时间对象 {@link Temporal}
     * @param pattern  格式
     * @return 格式化后的字符串
     * @throws DateTimeException 如果格式不正确，或无法按给定的格式应用格式化对象则抛出
     */
    public static String format(Temporal dateTime, String pattern) {
        if (dateTime == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return formatter.format(dateTime);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DateTimeException(e.getMessage(), e);
        }
    }

    // Date 与 LocalDateTime / ZonedDateTime 转换

    /**
     * 将 {@link LocalDateTime} 对象附加上系统默认的时区转换为 {@link ZonedDateTime}
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @return {@link ZonedDateTime} 对象
     */
    public static ZonedDateTime atSystemZone(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    }

    /**
     * 将 {@link LocalDateTime} 对象附加上 UTC 0 时区(UTC+0000)转换为 {@link ZonedDateTime}
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @return {@link ZonedDateTime} 对象
     */
    public static ZonedDateTime atUtcZone(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return ZonedDateTime.of(localDateTime, ZERO_ZONE_ID);
    }

    /**
     * 将 {@link Date} 对象转换为使用系统默认时区的 {@link ZonedDateTime}
     *
     * @param date {@link Date} 对象
     * @return {@link ZonedDateTime} 对象
     */
    public static ZonedDateTime atSystemZone(Date date) {
        return atZone(date, ZoneId.systemDefault());
    }

    /**
     * 将 {@link Date} 对象转换为使用 UTC 0 时区(UTC+0000)的 {@link ZonedDateTime}
     *
     * @param date {@link Date} 对象
     * @return {@link ZonedDateTime} 对象
     */
    public static ZonedDateTime atUtcZone(Date date) {
        return atZone(date, ZERO_ZONE_ID);
    }

    /**
     * 将 {@link Date} 对象转换为使用 zoneId 指定时区的 {@link ZonedDateTime}
     *
     * @param date   {@link Date} 对象
     * @param zoneId 指定转换后日期时间所在的时区
     * @return {@link ZonedDateTime} 对象
     */
    public static ZonedDateTime atZone(Date date, ZoneId zoneId) {
        if (date == null || zoneId == null) return null;
        return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * 将 {@link Date} 对象转换为 {@link LocalDateTime} 对象
     * <p>转换后的日期时间是在系统默认时区 {@link ZoneId#systemDefault()} 下对应的日期时间。
     *
     * @param date {@link Date} 对象
     * @return 含相同日期时间部分的 {@link LocalDateTime} 对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    /**
     * 将 {@link Date} 对象转换为 {@link LocalDateTime} 对象
     * <p>转换后的日期时间是在 zoneId 指定时区下的对应日期时间。
     *
     * @param date   {@link Date} 对象
     * @param zoneId 指定转换后日期时间所在的时区
     * @return 含相同日期时间部分的 {@link LocalDateTime} 对象
     */
    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        if (date == null || zoneId == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * 将 {@link ZonedDateTime} 对象转换为 {@link Date} 对象
     *
     * @param zonedDateTime {@link ZonedDateTime} 对象
     * @return {@link Date} 对象
     * @throws IllegalArgumentException 如果 zonedDateTime 代表的时间太大而不能转为 Date 时抛出
     */
    public static Date toDate(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 将 {@link LocalDateTime} 转换为 {@link Date} 对象，localDateTime 是在系统默认时区下显示的时间
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @return {@link Date} 对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return toDate(localDateTime, ZoneId.systemDefault());
    }

    /**
     * 将 {@link LocalDateTime} 转换为 {@link Date} 对象，localDateTime 是在 zoneId 指定的时区下显示的时间
     *
     * @param localDateTime {@link LocalDateTime} 对象
     * @param zoneId        指定 localDateTime 所属的时区
     * @return {@link Date} 对象
     */
    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null || zoneId == null) return null;
        return Date.from(ZonedDateTime.of(localDateTime, zoneId).toInstant());
    }

    protected DateTimeUtils() {
    }
}
