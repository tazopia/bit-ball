package spoon.gameZone;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ZoneUtils {

    private static final LocalTime start = LocalTime.of(6, 0, 0);
    private static final LocalTime end = LocalTime.of(0, 2, 0); // 끝날대 마지막 회차 결과를 가져와야 한다.

    public static boolean enabledPower() {
        LocalTime time = LocalTime.now();
        return !time.isBefore(start) || !time.isAfter(end);
    }

    public static boolean enabledPower(LocalDateTime date) {
        LocalTime time = date.toLocalTime();
        return !time.isBefore(start) || !time.isAfter(end);
    }
}
