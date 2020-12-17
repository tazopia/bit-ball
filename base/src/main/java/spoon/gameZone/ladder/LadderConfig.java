package spoon.gameZone.ladder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LadderConfig {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax = 1;

    private boolean oddeven;

    private boolean start;

    private boolean line;

    private boolean lineStart;

    /**
     * gameCode - 베팅시 betItem special 에 들어갈 코드
     * 0:홀(oddeven), 1:짝(oddeven), 2:좌(start), 3:우(start), 4:3줄(line), 5:4줄(line)
     * 6:3줄/좌(line3Start), 7:3줄/우(line3Start), 8:4줄/좌(line4Start), 9:4줄/우(line4Start)
     */
    private double[] odds = new double[10];

    private int[] win = {0, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {0, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public LadderConfig() {
        this.zoneMaker = new ZoneMaker(5);
    }

}
