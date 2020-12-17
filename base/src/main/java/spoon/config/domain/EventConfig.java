package spoon.config.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventConfig {

    // 출첵 기준점
    private long amount;

    private int[] daily = new int[5];

    private long[] money = new long[5];

    private String[] memo = new String[5];

}
