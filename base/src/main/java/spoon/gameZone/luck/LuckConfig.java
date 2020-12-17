package spoon.gameZone.luck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LuckConfig {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax;

    private boolean player1;

    private boolean player2;

    private boolean player3;

    private boolean color;

    private boolean pattern;

    /**
     * 0:승(player1), 1:무(player1), 2:패(player1), 3:승(player2), 4:무(player2), 5:패(player2), 6:승(player3), 7:무(player3), 8:패(player3)
     * 9:레드(color) 10:블랙(color), 11:스페이드(pattern1), 12:하트(pattern1), 13:크로바(pattern2), 14:다이아(pattern2)
     */
    private double[] odds = new double[15];

    private int[] win = {3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public LuckConfig() {
        this.zoneMaker = new ZoneMaker(2);
    }

}
