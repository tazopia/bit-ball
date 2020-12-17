package spoon.gameZone.bitcoin3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import spoon.bot.support.ZoneMaker;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Bitcoin3Config {

    @JsonIgnore
    private ZoneMaker zoneMaker;

    private boolean enabled;

    // 결과처리가 잘 되었는지 체크 (관리자에서 사용)
    private long result;

    private int betTime;

    // 회차별 베팅 숫자
    private int betMax = 1;

    // 최소 롤링 배당
    private double minOdds;

    private boolean hi_oe;

    private boolean hi_ou;

    private boolean lo_oe;

    private boolean lo_ou;

    private boolean hi_oe_ou;

    private boolean lo_oe_ou;


    /**
     * gameCode - 베팅시 betItem special 에 들어갈 코드
     * 0:상한 홀(hi_oe), 1:상한 짝(hi_oe), 2:상한 오버(hi_ou), 3:상한 언더(hi_ou),
     * 4:하향 홀(lo_oe), 5:하향 짝(lo_oe), 6:하향 오버(lo_ou), 7:하향 언더(lo_ou),
     * 8:상향 홀 오버(hi_odd_ou), 9:상향 홀 언더(hi_odd_ou), 10:상향 짝 오버(hi_even_ou), 11:상향 짝 언더(hi_even_ou),
     * 12:상향 홀 오버(lo_odd_ou), 13:상향 홀 언더(lo_odd_ou), 14:상향 짝 오버(lo_even_ou), 15:상향 짝 언더(lo_even_ou),
     */
    private double[] odds = new double[16];

    private int[] win = {3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};

    private int[] max = {1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000};

    private int[] min = {5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

    public Bitcoin3Config() {
        this.zoneMaker = new ZoneMaker(3);
    }
}
