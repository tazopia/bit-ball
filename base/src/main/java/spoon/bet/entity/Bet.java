package spoon.bet.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.domain.MenuCode;
import spoon.member.domain.Role;
import spoon.member.domain.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "BET", indexes = {
        @Index(name = "IDX_userid", columnList = "userid"),
        @Index(name = "IDX_agency1", columnList = "agency1"),
        @Index(name = "IDX_agency2", columnList = "agency2"),
        @Index(name = "IDX_agency3", columnList = "agency3"),
        @Index(name = "IDX_agency4", columnList = "agency4"),
        @Index(name = "IDX_menuCode", columnList = "menuCode"),
        @Index(name = "IDX_betDate", columnList = "betDate")
})
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "betId")
    private List<BetItem> betItems = new ArrayList<>();

    @Column(columnDefinition = "nvarchar(64)", nullable = false)
    private String userid;

    @Column(columnDefinition = "nvarchar(64)")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private int level;

    private boolean black;

    @Column(columnDefinition = "nvarchar(64)")
    private String agency1 = "";

    @Column(columnDefinition = "nvarchar(64)")
    private String agency2 = "";

    @Column(columnDefinition = "nvarchar(64)")
    private String agency3 = "";

    @Column(columnDefinition = "nvarchar(64)")
    private String agency4 = "";

    @Column(columnDefinition = "nvarchar(64)")
    private String recommender = "";

    private long betMoney;

    private long expMoney;

    private long hitMoney;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private MenuCode menuCode;

    private int betCount;

    private int hitCount;

    private int loseCount;

    private int cancelCount;

    private double betOdds;

    private double hitOdds;

    // 원폴더일 경우는 최종 배당에서 빼기를, 다폴더일 경우는 곱하기를 해 준다.
    private double eventOdds;

    @Temporal(TemporalType.TIMESTAMP)
    private Date betDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date closingDate;

    @Column(length = 32)
    private String result = "대기";

    // 머니나 포인트가 지급이 되었다면 true
    private boolean payment;

    // 사용자가 취소할 때 true
    private boolean cancel;

    // 관리자가 취소할 때 false
    private boolean enabled = true;

    // 사용자가 삭제할 때 true
    private boolean deleted;

    // 결과 처리가 다 되었을때 true
    private boolean closing;

    // 보험 적용 여부
    private boolean balance;

    @Column(length = 64)
    private String ip;

    //--------------------------------------------------------

    public Bet(User user) {
        this.userid = user.getUserid();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.level = user.getLevel();
        this.black = user.isBlack();
        this.agency1 = user.getAgency1();
        this.agency2 = user.getAgency2();
        this.agency3 = user.getAgency3();
        this.agency4 = user.getAgency4();
    }

    public boolean isCanCancel() {
        if (this.cancel || this.closing || this.menuCode == MenuCode.LIVE) return false;

        Calendar calendar = Calendar.getInstance();
        // 베팅시각 이내
        calendar.setTime(this.betDate);
        calendar.add(Calendar.MINUTE, Config.getGameConfig().getCancelBetTime());
        if ((new Date()).after(calendar.getTime())) {
            return false;
        }

        // 경기시작 이내
        calendar.setTime(this.startDate);
        calendar.add(Calendar.MINUTE, -Config.getGameConfig().getCancelGameTime());

        if (this.menuCode == MenuCode.BITCOIN1) calendar.add(Calendar.MINUTE, -1);
        if (this.menuCode == MenuCode.BITCOIN3) calendar.add(Calendar.MINUTE, -3);
        if (this.menuCode == MenuCode.BITCOIN5) calendar.add(Calendar.MINUTE, -5);

        return !(new Date()).after(calendar.getTime());
    }

    public boolean isStart() {
        return this.startDate.before(new Date());
    }

    public String getResultString() {
        if ("대기".equals(this.result)) {
            if (this.startDate.after(new Date())) {
                return "대기중";
            } else {
                return "진행중";
            }
        } else {
            return this.result;
        }
    }

    public String getResultStringCss() {
        if ("대기".equals(this.result)) {
            if (this.startDate.after(new Date())) {
                return "";
            } else {
                return "ing";
            }
        } else {
            switch (this.result) {
                case "적중":
                    return "win";
                case "미적중":
                    return "lose";
                case "적특":
                    return "hit";
                case "취소":
                case "베팅취소":
                case "진행중취소":
                    return "cancel";
                default:
                    return "";
            }
        }
    }

    boolean betResultOdds() {
        this.hitCount = 0;
        this.loseCount = 0;
        this.cancelCount = 0;

        BigDecimal betOdds = BigDecimal.valueOf(1.0);

        for (BetItem betItem : this.betItems) {
            if (betItem.isCancel()) { // 취소
                this.cancelCount++;
            } else if ("적중".equals(betItem.getResult())) {
                this.hitCount++;
                betOdds = betOdds.multiply(BigDecimal.valueOf(betItem.getOdds()));
            } else if ("적특".equals(betItem.getResult())) {
                this.hitCount++;
            } else if ("미적중".equals(betItem.getResult())) {
                this.loseCount++;
                betOdds = BigDecimal.valueOf(0);
            }
        }

        // 미적중
        if (this.loseCount > 0) {
            this.result = "미적중";
            this.hitMoney = 0;
            this.hitOdds = 0;
            this.closing = true;
            return true;
        }

        // 취소
        if (this.cancelCount == this.betCount) {
            this.cancel = true;
            this.result = "취소";
            this.hitMoney = this.betMoney;
            this.hitOdds = 1;
            this.closing = true;
            return true;
        }

        // 적중
        if (this.hitCount + this.cancelCount == this.betCount) {
            this.result = "적중";
            if (MenuCode.isSports(this.menuCode)) {
                betOdds = this.betCount == 1 ? oneBonus(betOdds) : bonusOdds(betOdds);
            }
            this.hitOdds = betOdds.setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
            this.hitMoney = (long) (this.betMoney * this.hitOdds);
            this.closing = true;
            return true;
        }

        // 종료된 경기가 다시 대기상태로 갈 수도 있다.
        this.result = "대기";
        this.closing = false;
        return false;
    }

    boolean betZoneOdds() {
        this.hitCount = 0;
        this.loseCount = 0;
        this.cancelCount = 0;
        this.closing = true;

        BetItem betItem = this.betItems.get(0);

        if (betItem.isCancel()) { // 취소
            this.cancelCount = 1;
            this.cancel = true;
            this.result = "취소";
            this.hitMoney = this.betMoney;
            this.hitOdds = 1;
        } else if ("적중".equals(betItem.getResult())) {
            this.hitCount = 1;
            this.hitOdds = this.betOdds;
            this.hitMoney = (long) (this.betOdds * this.betMoney);
            this.result = "적중";
        } else if ("적특".equals(betItem.getResult())) {
            this.hitCount = 1;
            this.result = "적특";
            this.hitMoney = this.betMoney;
            this.hitOdds = 1;
        } else if ("미적중".equals(betItem.getResult())) {
            this.loseCount = 1;
            this.result = "미적중";
            this.hitMoney = 0;
            this.hitOdds = 0;
        }
        return true;
    }

    public void betExpOdds() {
        BigDecimal betOdds = betItems.stream().filter(x -> !x.isCancel()).map(x -> BigDecimal.valueOf(x.getOdds())).reduce(BigDecimal.ONE, BigDecimal::multiply);
        // 스포츠 게임만 원폴더 배당하락, 다폴더 배당상승이 있다.
        if (MenuCode.isSports(this.menuCode)) {
            // 최소 배당
            double minOdds = betItems.stream().map(BetItem::getOdds).min(Double::compareTo).orElse(0D);
            if (!Config.getGameConfig().isEnabledMinOdds() || minOdds >= Config.getGameConfig().getMinOdds()) {
                betOdds = this.betCount == 1 ? oneBonus(betOdds) : bonusOdds(betOdds);
            }
        }
        this.betOdds = betOdds.setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
        this.expMoney = (long) (this.betMoney * this.betOdds);
    }

    private BigDecimal bonusOdds(BigDecimal betOdds) {
        boolean hasBonus = false;

        switch (this.menuCode) {
            case CROSS:
            case MATCH:
            case HANDICAP:
                hasBonus = Config.getGameConfig().getBonus()[0];
                break;
            case SPECIAL:
                hasBonus = Config.getGameConfig().getBonus()[1];
                break;
            case LIVE:
                hasBonus = Config.getGameConfig().getBonus()[2];
                break;
        }

        if (hasBonus && Config.getGameConfig().getBonusOdds()[this.betCount] > 1) {
            this.eventOdds = Config.getGameConfig().getBonusOdds()[this.betCount];
            betOdds = betOdds.multiply(BigDecimal.valueOf(Config.getGameConfig().getBonusOdds()[this.betCount]));
        }
        return betOdds;
    }

    // 최종 배당이 1일 경우는 보너스배당을 적용하지 않는다. 취소, 적특일 경우
    private BigDecimal oneBonus(BigDecimal betOdds) {
        if (Config.getGameConfig().getBonusOne() > 0 && this.menuCode.getValue() <= MenuCode.SPORTS.getValue() && betOdds.compareTo(BigDecimal.ONE) != 0) {
            this.eventOdds = -Config.getGameConfig().getBonusOne();
            betOdds = betOdds.subtract(BigDecimal.valueOf(Config.getGameConfig().getBonusOne()));
        }
        return betOdds;
    }

    /**
     * 전체 베팅 취소 하기
     */
    public long[] allCancel() {
        int index = 0;
        long[] gameIds = new long[this.betItems.size()];
        for (BetItem item : this.betItems) {
            item.setCancel(true);
            item.setClosing(true);
            item.setResult(WebUtils.role() == Role.USER ? "베팅취소" : (item.isStart() ? "진행중취소" : "취소"));
            gameIds[index] = item.getGameId();
            index++;
        }
        makeCancel();
        return gameIds;
    }

    /**
     * 베팅중 특정 아이템 취소하기
     */
    public Long itemCancel(Long itemId) {
        long gameId = 0;
        for (BetItem item : this.betItems) {
            if (item.getId().equals(itemId)) {
                item.setCancel(true);
                item.setClosing(true);
                item.setResult(WebUtils.role() == Role.USER ? "베팅취소" : (item.isStart() ? "진행중취소" : "취소"));
                gameId = item.getGameId();
                break;
            }
        }
        this.betResultOdds();
        return gameId;
    }

    private void makeCancel() {
        this.cancel = true;
        this.result = WebUtils.role() == Role.USER ? "베팅취소" : (this.isStart() ? "진행중취소" : "취소");
        this.hitMoney = this.betMoney;
        this.hitOdds = 1;
        this.closing = true;
    }

    public void updateGameDate() {
        this.startDate = this.betItems.stream().map(BetItem::getGameDate).min(Date::compareTo).get();
        this.endDate = this.betItems.stream().map(BetItem::getGameDate).max(Date::compareTo).get();
    }
}
