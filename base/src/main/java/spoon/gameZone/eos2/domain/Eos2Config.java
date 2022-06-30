package spoon.gameZone.eos2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Eos2Config {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax = 1;

    private boolean oddeven;

    private boolean pb_oddeven;

    private boolean overunder;

    private boolean pb_overunder;

    private boolean size;

    // 11, 12, 13, 14
    private boolean oe_ou;

    // 15,16,17 / 18,19,20
    private boolean oe_size;

    // 21, 22, 23, 24
    private boolean pb_oe_ou;

    /**
     * 0:홀(oddeven), 1:짝(oddeven), 2:홀(pb_oddeven), 3:짝(pb_oddeven)
     * 4:오버(overunder), 5:언더(overunder), 6:오버(pb_overunder), 7:언더(pb_overunder)
     * 8:대(size), 9:중(size), 10:소(size)
     * 11: 일반홀오버(oe_ou), 12:일반홀언더 (oe_ou), 13: 일반짝오버 (oe_ou), 14: 일반짝언더 (oe_ou)
     * 15~20: 홀 - 대중소(oe_size), 짝 대중소(oe_size)
     */
    private double[] odds = new double[25];

    private int[] win = {3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public Eos2Config() {
        this.zoneMaker = new ZoneMaker(2);
    }

}
