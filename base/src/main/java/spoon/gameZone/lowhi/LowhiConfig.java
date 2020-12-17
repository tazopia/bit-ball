package spoon.gameZone.lowhi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LowhiConfig {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax;

    private boolean oddeven;

    private boolean lowhi;

    private boolean lowhiOddeven;

    /**
     * 0:홀(oddeven), 1:짝(oddeven), 2:로우(lowhi), 3:하이(lowhi)
     * 4:로우/홀(lowOddeven), 5:로우/짝(lowOddeven), 6:하이/홀(hiOddeven), 7:하이/짝(hiOddeven)
     */
    private double[] odds = new double[8];

    private int[] win = {3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public LowhiConfig() {
        this.zoneMaker = new ZoneMaker(3);
    }

}
