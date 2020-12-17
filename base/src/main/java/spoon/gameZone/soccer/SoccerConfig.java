package spoon.gameZone.soccer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SoccerConfig {

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    // 베팅 마감 시간
    private int betTime;

    // 회차별 베팅 숫자
    private int betMax;

    private boolean ma;

    private boolean ou;

    private boolean ah;

    // 배당 일괄 조정
    private double odds = 0.0;

    private double oddsUpDown = 100;

    private double minOdds = 1.35;

    private double maxOdds = 12.0;

    private int[] win = {0, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {0, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

}
