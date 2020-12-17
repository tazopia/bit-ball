package spoon.bet.domain;

import lombok.Data;
import spoon.common.utils.DateUtils;
import spoon.game.domain.GameResult;
import spoon.game.domain.MenuCode;

import java.util.Date;

public class BetDto {

    @Data
    public static class BetGame {
        private long betMoney;
        private MenuCode menuCode;
        private Long[] ids;
        private String[] bets;
        private Double[] odds;
        private boolean force;
    }

    @Data
    public static class Command {
        private Long gameId;
        private String betDate = "";
        private String role = "";
        private MenuCode menuCode;
        private String result = "";
        private String username = "";
        private boolean match;
        private String orderBy = "";
        private String userid = "";
        private String ip = "";
    }

    @Data
    public static class UserCommand {
        private String userid;
        private String sDate = DateUtils.format(DateUtils.beforeDays(7));
        private String eDate = DateUtils.format(new Date());
    }

    @Data
    public static class SellerCommand {
        private String username;
        private boolean match;
        private String role;
        private String agency;
    }

    @Data
    public static class Delete {
        private String userid;
        private Long[] betIds;
    }

    @Data
    public static class Score {
        private int scoreHome;
        private int scoreAway;
        private GameResult gameResult;
    }

    @Data
    public static class ZoneAmount {
        private String gameCode;
        private int betZone;
        private long amount;
    }

    @Data
    public static class UserBet {
        private MenuCode menuCode;
        private long cnt;
        private long betMoney;
        private long hitMoney;
        private Date betDate;
    }

    @Data
    public static class BetRate {
        private String sdate;
        private String edate;
        private String orderBy = "";
        private String username;
        private boolean match;
        private int page = 1;
        private int size = 20;

        public BetRate() {
            this.sdate = DateUtils.todayString();
            this.edate = this.sdate;
        }

        public String getStart() {
            return this.sdate.replaceAll("\\.", "-");
        }

        public String getEnd() {
            return this.edate.replaceAll("\\.", "-");
        }
    }

    @Data
    public static class BetZoneCancel {
        private long id;
    }
}
