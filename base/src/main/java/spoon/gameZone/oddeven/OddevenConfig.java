package spoon.gameZone.oddeven;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OddevenConfig {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax;

    private boolean oddeven;

    private boolean overunder;

    private boolean pattern;

    /**
     * 0:홀(oddeven), 1:짝(oddeven), 2:오버(overunder), 3:언더(overunder)
     * 4:스페이드(pattern1), 5:하트(pattern1), 6:크로바(pattern2), 7:다이아(pattern2)
     */
    private double[] odds = new double[8];

    private int[] win = {3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public OddevenConfig() {
        this.zoneMaker = new ZoneMaker(1);
    }

}
