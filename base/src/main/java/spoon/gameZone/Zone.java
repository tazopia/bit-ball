package spoon.gameZone;

import lombok.Data;
import spoon.game.domain.MenuCode;

import java.util.Date;

@Data
public class Zone {
    private Long id;
    private String sdate;
    private MenuCode menuCode;

    // 홀짝, 승무패 등등 베팅 게임명
    private String gameCode;

    // 회차
    private String league;

    private String teamHome;
    private String teamAway;

    private double handicap;
    private double oddsHome;
    private double oddsAway;
    private double oddsDraw;

    private Date gameDate;
}
