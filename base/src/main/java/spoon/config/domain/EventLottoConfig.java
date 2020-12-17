package spoon.config.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventLottoConfig {

    private boolean enabled;

    // 금액 설정
    private long[] money = new long[10];

    // 확율 설정
    private int[] rate = new int[10];

    // 충전금액의 몇%
    private int max = 100;

    private long deposit = 10000;

    public int getSum() {
        return Arrays.stream(rate).sum();
    }
}
