package spoon.bet.domain;

import lombok.Data;
import spoon.game.domain.MenuCode;
import spoon.support.extend.DefaultHashMap;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BetUserInfo {

    private long cntSports;
    private long betSports;
    private long hitSports;
    private Date dateSports;

    private long cntZone;
    private long betZone;
    private long hitZone;
    private Date dateZone;

    private Map<MenuCode, BetDto.UserBet> betting = new DefaultHashMap<>(new BetDto.UserBet());

    public BetUserInfo(List<BetDto.UserBet> list) {
        for (BetDto.UserBet bet : list) {
            switch (bet.getMenuCode()) {
                case MATCH:
                case HANDICAP:
                case CROSS:
                case SPECIAL:
                case LIVE:
                case SPORTS:
                    cntSports += bet.getCnt();
                    betSports += bet.getBetMoney();
                    hitSports += bet.getHitMoney();
                    dateSports = bet.getBetDate();
                    break;
                default:
                    cntZone += bet.getCnt();
                    betZone += bet.getBetMoney();
                    hitZone += bet.getHitMoney();
                    dateZone = bet.getBetDate();
            }
            betting.put(bet.getMenuCode(), bet);
        }
    }
}
