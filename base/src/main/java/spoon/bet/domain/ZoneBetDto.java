package spoon.bet.domain;

import lombok.Getter;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.common.utils.DateUtils;

@Getter
public class ZoneBetDto {

    private final long id;
    private final long betMoney;
    private final long expMoney;
    private final long hitMoney;
    private final String date;
    private final String time;
    private final String result;
    private final String className;
    private final boolean cancel;

    private String hname;
    private String aname;
    private String league;
    private String bet;
    private double odds;


    public ZoneBetDto(Bet bet) {
        this.id = bet.getId();
        this.betMoney = bet.getBetMoney();
        this.expMoney = bet.getExpMoney();
        this.hitMoney = bet.getHitMoney();
        this.date = DateUtils.format(bet.getBetDate(), "MM/dd");
        this.time = DateUtils.format(bet.getBetDate(), "HH:mm:ss");
        this.result = bet.getResult().contains("취소") ? "취소" : bet.getResult();
        this.cancel = bet.isCanCancel();

        switch (this.result) {
            case "적중":
                this.className = "win";
                break;
            case "미적중":
                this.className = "lose";
                break;
            case "적특":
                this.className = "tie";
                break;
            case "대기":
                this.className = "";
                break;
            default:
                this.className = "cancel";
        }

        if (bet.getBetItems() == null || bet.getBetItems().size() == 0)
            return;

        BetItem item = bet.getBetItems().get(0);
        this.league = item.getLeague();
        this.odds = item.getOdds();
        this.hname = item.getTeamHome();
        this.aname = item.getTeamAway();
        this.bet = item.getBetTeam();
    }
}
