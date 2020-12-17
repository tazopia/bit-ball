package spoon.gameZone.newSnail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class NewSnailConfig {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax = 1;

    private boolean oddeven;

    private boolean overunder;

    private boolean ranking;

    /**
     * gameCode - 베팅시 betItem special 에 들어갈 코드
     * 0:홀(oddeven), 1:짝(oddeven), 2:오버(overunder), 3:언더(overunder), 4:ranking(1), 5:ranking(2), 6:ranking(3), 7:ranking(4)
     */
    private double[] odds = new double[8];

    private int[] win = {0, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {0, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public NewSnailConfig() {
        this.zoneMaker = new ZoneMaker(3);
    }

}
