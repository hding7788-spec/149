package ext.casc.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimestampConverter {

    public static String toChinaStandardTime(long timestamp) {
        return toChinaStandardTime(timestamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toChinaStandardTime(long timestamp, String pattern) {
        // 使用Java 8+的java.time包（推荐）
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * 传统方式：使用SimpleDateFormat将时间戳转换为中国时间
     * @param timestamp 时间戳（毫秒）
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public static String toChinaTimeTraditional(long timestamp, String pattern) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(date);
    }

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis(); // 当前时间戳

        // 示例1：默认格式
        String defaultFormat = toChinaStandardTime(timestamp);
        System.out.println("默认格式: " + defaultFormat); // 输出: 2025-06-18 14:30:00

        // 示例2：自定义格式
        String customFormat = toChinaStandardTime(timestamp, "yyyy/MM/dd HH:mm:ss");
        System.out.println("自定义格式: " + customFormat); // 输出: 2025/06/18 14:30:00

        // 示例3：传统方式
        String traditionalFormat = toChinaTimeTraditional(timestamp, "yyyy年MM月dd日 HH时mm分ss秒");
        System.out.println("传统方式: " + traditionalFormat); // 输出: 2025年06月18日 14时30分00秒
    }
}
