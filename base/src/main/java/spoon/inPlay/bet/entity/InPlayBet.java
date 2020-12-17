package spoon.inPlay.bet.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;
import spoon.inPlay.bet.domain.InPlayBetDto;
import spoon.inPlay.odds.entity.InPlayGame;
import spoon.inPlay.odds.entity.InPlayOdds;
import spoon.inPlay.odds.entity.InPlayOddsDone;
import spoon.member.domain.Role;
import spoon.member.entity.Member;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "INPLAY_BET")
public class InPlayBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(length = 64, nullable = false)
    private String userid;

    @Nationalized
    @Column(length = 64)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Role role;

    private int level;

    private boolean black;

    @Nationalized
    @Column(length = 64)
    private String agency1 = "";

    @Nationalized
    @Column(length = 64)
    private String agency2 = "";

    @Nationalized
    @Column(length = 64)
    private String agency3 = "";

    @Nationalized
    @Column(length = 64)
    private String agency4 = "";

    @Nationalized
    @Column(length = 64)
    private String recommender = "";

    private long betMoney;

    private long expMoney;

    // .25, .75
    private long hitMoney;

    private double betOdds;

    // .25, .75
    private double hitOdds;

    @Temporal(TemporalType.TIMESTAMP)
    private Date betDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gameDate;

    @Column(length = 32)
    private String status = "대기";

    // 머니나 포인트가 지급이 되었다면 true
    private boolean payment;

    // 사용자가 취소할 때 true
    private boolean cancel;

    // 유효베팅일때 true, 처음 등록시 false
    private boolean enabled;

    // 사용자가 삭제할 때 true
    private boolean deleted;

    // 결과 처리가 다 되었을때 true
    private boolean closing;

    // -1 : 취소
    // 1 : 미당첨
    // 2 : 당첨
    // 3 : 적특
    // 4 : 50% 미당첨
    // 5 : 50% 당첨
    //4,5 의 경우는 주로 0.25 핸디캡 등에 사용 됩니다
    private int result;

    @Column(length = 64)
    private String ip;

    private long fixtureId;

    private long marketId;

    private long oddsId;

    @Nationalized
    @Column(length = 64)
    private String sports;

    @Nationalized
    @Column(length = 128)
    private String league;

    @Nationalized
    private String teamHome;

    @Nationalized
    private String teamAway;

    @Nationalized
    private String name;

    @Nationalized
    private String oname;

    private String score;

    private String corner;

    private String line;

    public void addBet(Member member, InPlayBetDto.Bet betDto, InPlayGame game, InPlayOdds odds) {
        userid = member.getUserid();
        nickname = member.getNickname();
        role = member.getRole();
        level = member.getLevel();
        black = member.isBlack();
        agency1 = member.getAgency1();
        agency2 = member.getAgency2();
        agency3 = member.getAgency3();
        agency4 = member.getAgency4();
        recommender = member.getRecommender();
        betMoney = betDto.getMoney();
        betOdds = betDto.getOdds();
        fixtureId = betDto.getFixtureId();
        oddsId = betDto.getId();
        marketId = odds.getMarketId();
        ip = member.getLoginIp();
        sports = game.getSname();
        league = game.getLeague();
        teamHome = game.getHname();
        teamAway = game.getAname();
        name = odds.getName();
        oname = odds.getOname();
        line = odds.getLine();
        betDate = new Date();
        gameDate = game.getSdate();
        score = betDto.getScore();
        corner = betDto.getCorner();
        betMoney = betDto.getMoney();
        betOdds = betDto.getOdds();
        expMoney = (long) (betDto.getMoney() * betDto.getOdds());
        status = "베팅접수";
    }

    public void updateScore(InPlayOddsDone done) {
        // -1 : 취소
        // 1 : 미당첨
        // 2 : 당첨
        // 3 : 적특
        // 4 : 50% 미당첨
        // 5 : 50% 당첨
        //4,5 의 경우는 주로 0.25 핸디캡 등에 사용 됩니다
        this.result = done.getSettlement();
        switch (this.result) {
            case -1:
                this.cancel = true;
                this.status = "취소";
                this.hitOdds = 1D;
                this.hitMoney = this.betMoney;
                break;
            case 1:
                this.status = "미적중";
                this.hitMoney = 0;
                this.hitOdds = 0;
                break;
            case 2:
                this.status = "적중";
                this.hitOdds = this.betOdds;
                this.hitMoney = (long) ((Math.floor(this.betOdds * 100) / 100) * this.betMoney);
                break;
            case 3:
                this.cancel = true;
                this.status = "적특";
                this.hitOdds = 1D;
                this.hitMoney = this.betMoney;
                break;
            case 4: // 50% 미당첨
                this.status = "50%미당첨";
                this.hitMoney = (long) ((Math.floor(this.betOdds * 100) / 100) * (this.betMoney / 2));
                break;
            case 5:
                // 50% 당첨
                this.status = "50%당첨";
                this.hitMoney = (long) (((Math.floor(this.betOdds * 100) / 100) / 2) * this.betMoney);
                break;
            default:
                throw new IllegalArgumentException("결과가 정확하지 않습니다.");
        }

        this.payment = true;
        this.closing = true;
    }

    public void notAccept() {
        this.enabled = true;
        this.cancel = true;
        this.closing = true;
        this.payment = true;
        this.hitOdds = 1D;
        this.status = "베팅취소";
        this.hitMoney = this.betMoney;
    }

    public void accept() {
        this.enabled = true;
        this.status = "베팅완료";
    }

    public void goHit() {
        this.enabled = true;
        this.status = "적중";
        this.hitOdds = this.betOdds;
        this.hitMoney = (long) ((Math.floor(this.betOdds * 100) / 100) * this.betMoney);
        this.closing = true;
        this.payment = true;
        this.cancel = false;
        this.result = 2;
    }

    public void goLose() {
        this.enabled = true;
        this.status = "미적중";
        this.hitOdds = 0;
        this.hitMoney = 0;
        this.closing = true;
        this.payment = true;
        this.cancel = false;
        this.result = 1;
    }

    public void goException() {
        this.enabled = true;
        this.status = "적특";
        this.hitOdds = 1;
        this.hitMoney = this.betMoney;
        this.closing = true;
        this.payment = true;
        this.cancel = false;
        this.result = 3;
    }

    public void goCancel() {
        this.enabled = true;
        this.status = "취소";
        this.hitOdds = 1;
        this.hitMoney = this.betMoney;
        this.closing = true;
        this.payment = true;
        this.cancel = true;
        this.result = -1;
    }

    public void userDelete() {
        this.deleted = true;
    }

    public String getCss() {
        switch (this.status) {
            case "적중":
                return "win";
            case "적특":
                return "hit";
            case "취소":
            case "베팅취소":
                return "cancel";
            case "미적중":
                return "lose";
            default:
                return "";
        }
    }
}
