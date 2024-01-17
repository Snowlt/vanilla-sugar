package sugar.tool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.MethodName.class)
class DateTimeUtilsTest {

    final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    void timestamp() {
        final long timestamp = 1704067954;
        final long milliTimestamp = 1704067954567L;
        LocalDateTime exceptedSec = LocalDateTime.of(2024, 1, 1, 0, 12, 34);
        LocalDateTime exceptedMilli = LocalDateTime.of(2024, 1, 1, 0, 12, 34,567_000_000);
        useUtc8();
        assertEquals(exceptedSec, DateTimeUtils.unixTimestampToDateTime(timestamp));
        assertEquals(exceptedMilli, DateTimeUtils.milliTimestampToDateTime(milliTimestamp));
        useUtc0();
        assertEquals(exceptedSec, DateTimeUtils.unixTimestampToDateTime(timestamp));
        assertEquals(exceptedMilli, DateTimeUtils.milliTimestampToDateTime(milliTimestamp));
    }

    @Test
    void parseToDate() {
        assertNull(DateTimeUtils.parseToDate(null, DATE_TIME_FORMAT));
        Date expected = parse("2024-01-01 00:12:34 Z");
        useUtc0();
        Date date = DateTimeUtils.parseToDate("2024-01-01T00:12:34", DATE_TIME_FORMAT);
        assertEquals(expected, date);
        useUtc8();
        Date date2 = DateTimeUtils.parseToDate("2024-01-01T08:12:34", DATE_TIME_FORMAT);
        assertEquals(expected, date2);
    }

    @Test
    void parseToLocalDateTime() {
        assertNull(DateTimeUtils.parseToLocalDateTime(null, DATE_TIME_FORMAT));
        LocalDateTime expected = LocalDateTime.of(2024, 1, 1, 21, 12, 34);
        LocalDateTime dateTime = DateTimeUtils.parseToLocalDateTime("2024-01-01 21:12:34", "yyyy-MM-dd HH:mm:ss");
        assertEquals(expected, dateTime);
        LocalDateTime expected2 = LocalDateTime.of(2024, 1, 1, 0, 11, 22);
        LocalDateTime dateTime2 = DateTimeUtils.parseToLocalDateTime("2024-1-1T00:11:22+0900",
                "yyyy-M-d'T'HH:mm:ssZ");
        assertEquals(expected2, dateTime2);
    }

    @Test
    void parseToZonedDateTime() {
        final String pattern = "yyyy-M-d'T'HH:mm:ssX";
        assertNull(DateTimeUtils.parseToZonedDateTime(null, pattern));
        ZonedDateTime dateTime = DateTimeUtils.parseToZonedDateTime("2024-1-1T00:11:22+0800", pattern);
        ZonedDateTime expected = ZonedDateTime.of(2024, 1, 1, 0, 11, 22,
                0, ZoneId.of("Asia/Shanghai"));
        // ZonedDateTime 中时区名字不同，但是 ZoneOffset 相同所以是等效的，可以使用 Instant 类型比较作为替代
        assertEquals(expected.toInstant(), dateTime.toInstant());
        ZonedDateTime dateTime2 = DateTimeUtils.parseToZonedDateTime("2024-1-1T00:11:22+0900", pattern);
        ZonedDateTime expected2 = ZonedDateTime.of(2024, 1, 1, 0, 11, 22,
                0, ZoneId.of("Asia/Tokyo"));
        assertEquals(expected2.toInstant(), dateTime2.toInstant());
    }

    @Test
    void zonedDateTimeToDate() {
        assertNull(DateTimeUtils.toDate((ZonedDateTime) null));
        LocalDateTime testedLdt = LocalDateTime.of(2024, 1, 1, 0, 11, 22);
        assertEquals(parse("2024-01-01 00:11:22 Z"),
                DateTimeUtils.toDate(ZonedDateTime.of(testedLdt, ZoneId.of("UTC+0"))));
        assertEquals(parse("2024-01-01 00:11:22 +0800"),
                DateTimeUtils.toDate(ZonedDateTime.of(testedLdt, ZoneId.of("Asia/Shanghai"))));
        assertEquals(parse("2024-01-01 00:11:22 +0900"),
                DateTimeUtils.toDate(ZonedDateTime.of(testedLdt, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    void localDateTimeToDate() {
        assertNull(DateTimeUtils.toDate((LocalDateTime) null));
        Date expected = parse("2024-01-01 08:12:34 +0800");
        // manual specifying the timezone
        LocalDateTime utc0 = LocalDateTime.of(2024, 1, 1, 0, 12, 34);
        LocalDateTime utc8 = utc0.plusHours(8);
        LocalDateTime utc9 = utc0.plusHours(9);
        assertEquals(expected, DateTimeUtils.toDate(utc0, ZoneId.of("UTC+0")));
        assertEquals(expected, DateTimeUtils.toDate(utc8, ZoneId.of("UTC+0800")));
        assertEquals(expected, DateTimeUtils.toDate(utc9, ZoneId.of("UTC+0900")));
        // depends on the system timezone
        useUtc0();
        assertEquals(expected, DateTimeUtils.toDate(LocalDateTime.of(2024, 1, 1, 0, 12, 34)));
        useUtc8();
        assertEquals(expected, DateTimeUtils.toDate(LocalDateTime.of(2024, 1, 1, 8, 12, 34)));
    }

    @Test
    void dateToLocalDateTime() {
        assertNull(DateTimeUtils.toLocalDateTime(null));
        Date testedDate = parse("2024-01-23 01:23:45 Z");
        // manual specifying the timezone
        LocalDateTime expectedUtc0 = LocalDateTime.of(2024, 1, 23, 1, 23, 45);
        LocalDateTime expectedUtc8 = expectedUtc0.plusHours(8);
        LocalDateTime expectedUtc9 = expectedUtc0.plusHours(9);
        assertEquals(expectedUtc0, DateTimeUtils.toLocalDateTime(testedDate, ZoneId.of("UTC+0")));
        assertEquals(expectedUtc8, DateTimeUtils.toLocalDateTime(testedDate, ZoneId.of("UTC+0800")));
        assertEquals(expectedUtc9, DateTimeUtils.toLocalDateTime(testedDate, ZoneId.of("UTC+0900")));
        // depends on the system timezone
        useUtc0();
        LocalDateTime dateTime = DateTimeUtils.toLocalDateTime(testedDate);
        assertEquals(LocalDateTime.of(2024,1,23, 1,23,45), dateTime);
        useUtc8();
        LocalDateTime dateTime2 = DateTimeUtils.toLocalDateTime(testedDate);
        assertEquals(LocalDateTime.of(2024,1,23, 9,23,45), dateTime2);
    }

    @Test
    void dateToZonedDateTime() {
        assertNull(DateTimeUtils.atSystemZone((Date) null));
        assertNull(DateTimeUtils.atUtcZone((Date) null));
        assertNull(DateTimeUtils.atZone(null, ZoneId.systemDefault()));
        // System timezone
        useUtc8();
        ZonedDateTime zonedDateTime = DateTimeUtils.atSystemZone(parse("2024-01-23 01:23:45 Z"));
        assertEquals(ZonedDateTime.of(2024, 1, 23, 9, 23, 45,
                0, ZoneId.of("GMT+0800")), zonedDateTime);
        // UTC 0
        useUtc0();
        ZonedDateTime zonedDateTime2 = DateTimeUtils.atUtcZone(parse("2024-01-23 09:23:45 +0800"));
        assertEquals(ZonedDateTime.of(2024, 1, 23, 1, 23, 45,
                0, ZoneId.of("UTC+0000")), zonedDateTime2);
        // Manual timezone
        ZoneId zoneId = ZoneId.of("UTC+0900");
        ZonedDateTime expected = LocalDateTime.of(2024, 1, 23, 9, 2, 3).atZone(zoneId);
        ZonedDateTime zonedDateTime3 = DateTimeUtils.atZone(parse("2024-01-23 00:02:03 +0000"), zoneId);
        assertEquals(expected, zonedDateTime3);
    }

    /**
     * Parse to date. Format example: <pre>
     * 2024-01-23 01:23:45 Z
     * 2024-01-01 18:11:22 +0800
     * </pre>
     */
    static Date parse(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");
        try {
            return dateFormat.parse(dateTime);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private void useUtc0() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    private void useUtc8() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

    private static final TimeZone backupedTimeZone = TimeZone.getDefault();

    @AfterEach
    void tearDown() {
        TimeZone.setDefault(backupedTimeZone);
    }

}