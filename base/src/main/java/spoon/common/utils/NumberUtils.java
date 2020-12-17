package spoon.common.utils;


import java.math.BigDecimal;
import java.util.Random;

public abstract class NumberUtils {

    /**
     * 랜덤 범위
     */
    public static int random(int begin, int end) {
        Random r = new Random();
        return begin + r.nextInt(end - begin + 1);
    }

    /**
     * 소숫점 2자리 버림
     *
     * @param odds
     * @return
     */
    public static double odds(double odds) {
        return BigDecimal.valueOf(odds).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
    }
}
