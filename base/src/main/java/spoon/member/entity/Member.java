package spoon.member.entity;

import lombok.Data;
import org.hibernate.annotations.Formula;
import spoon.common.utils.WebUtils;
import spoon.game.domain.MenuCode;
import spoon.gameZone.ZoneConfig;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.support.convert.CryptoConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "MEMBER", indexes = {
        @Index(name = "userid", columnList = "userid")
})
public class Member implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(64)", unique = true)
    private String userid;

    @Column(columnDefinition = "NVARCHAR(64)", unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private String pass;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private Role role;

    private int level;

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency1 = "";

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency2 = "";

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency3 = "";

    @Column(columnDefinition = "NVARCHAR(64)")
    private String agency4 = "";

    @Column(columnDefinition = "NVARCHAR(64)")
    private String recommender = "";

    @Column(length = 64)
    private String joinCode;

    @Column(length = 64)
    private String bank = "";

    @Convert(converter = CryptoConverter.class)
    private String account = "";

    @Convert(converter = CryptoConverter.class)
    private String depositor = "";

    @Convert(converter = CryptoConverter.class)
    private String bankPassword = "";

    @Convert(converter = CryptoConverter.class)
    private String phone = "";

    @Convert(converter = CryptoConverter.class)
    private String passKey = "";

    private long money;

    private long point;

    private long deposit;

    private long withdraw;

    @Formula("(deposit - withdraw)")
    private long change;

    // 로그인 가능 여부
    private boolean enabled;

    // 탈퇴 여부
    private boolean secession;

    // 블랙유저
    private boolean black;

    // 게시물 블럭
    private boolean block;

    // 보험 유저
    private boolean balanceLadder = true;
    private boolean balanceDari = true;
    private boolean balanceLowhi = true;
    private boolean balanceAladdin = true;
    private boolean balancePower = true;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String memo = "";

    @Column(length = 64)
    private String joinIp;

    @Column(length = 64)
    private String joinDomain;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date joinDate;

    @Column(length = 64)
    private String loginIp;

    @Column(length = 64)
    private String loginDevice;

    @Column(length = 64)
    private String loginDomain;

    private long loginCnt;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date loginDate;

    private long betSportsCnt;

    private long betSports;

    private long betZoneCnt;

    private long betZone;

    @Formula("(betSports + betZone)")
    private long betTotal;

    @Formula("(betSportsCnt + betZoneCnt)")
    private long betCntTotal;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date betDate;

    // 총판 대리점 지급율
    @Column(length = 16)
    private String rateCode = "";

    private double rateShare;

    private double rateSports;

    private double rateZone;

    // -------------------------------------

    private double powerMax;
    private double powerMin;

    private double powerLadder;
    private double keno;
    private double kenoLadder;

    private double btc3Max;
    private double btc3Min;
    private double btc5Max;
    private double btc5Min;

    private double casino;

    private double slot;

    // ---------------------------------------------

    public String getSunid() {
        return ZoneConfig.getSun().getPrefix() + String.format("%05d", this.id);
    }

    public String getCasinoEvoId() {
        return ZoneConfig.getCasinoEvo().getPrefix() + String.format("%06d", this.id);
    }

    public String getCasinoEvolutionId() {
        return ZoneConfig.getCasinoEvolution().getPrefix() + String.format("%05d", this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Member member = (Member) o;

        return userid != null ? userid.equals(member.userid) : member.userid == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userid != null ? userid.hashCode() : 0);
        return result;
    }

    public User getUser() {
        User user = new User();
        user.setUserid(this.userid);
        user.setNickname(this.nickname);
        user.setAgency1(this.agency1);
        user.setAgency2(this.agency2);
        user.setAgency3(this.agency3);
        user.setAgency4(this.agency4);
        user.setRole(this.role);
        user.setLevel(this.level);
        user.setLoginIp(this.loginIp);
        user.setBlack(this.black);
        user.setBlock(this.block);
        user.setSecession(this.secession);
        user.setEnabled(this.enabled);

        return user;
    }

    public User addMoney(MoneyCode moneyCode, long amount) {
        this.money += amount;
        switch (moneyCode) {
            case DEPOSIT: // 충전
            case DEPOSIT_ROLLBACK: // 충전 롤백
                this.deposit += amount;
                break;
            case WITHDRAW: // 환전
            case WITHDRAW_ROLLBACK: // 환전 롤백
                this.withdraw -= amount;
                break;
            case BET_SPORTS: // 스포츠 베팅
                this.betSports -= amount;
                this.betSportsCnt++;
                this.betDate = new Date();
                break;
            case BET_ZONE: // 실시간 베팅
                this.betZone -= amount;
                this.betZoneCnt++;
                this.betDate = new Date();
                break;
            case BET_SPORTS_ROLLBACK: // 스포츠 베팅 롤백
                this.betSports -= amount;
                this.betSportsCnt--;
                break;
            case BET_ZONE_ROLLBACK: // 실시간 베팅 롤백
                this.betZone -= amount;
                this.betZoneCnt--;
                break;
        }
        return getUser();
    }

    public User addPoint(PointCode pointCode, long point) {
        this.point += point;
        if (pointCode == PointCode.CHANGE && this.role == Role.USER) {
            this.deposit += point;
        }
        return getUser();
    }

    public User loginHistory() {
        this.loginCnt++;
        this.loginDate = new Date();
        this.loginIp = WebUtils.ip();
        this.loginDevice = WebUtils.device();
        this.loginDomain = WebUtils.domain();
        return getUser();
    }

    public double getRollingOdds(MenuCode code, double betOdds) {
        switch (code) {
            case POWER:
                return betOdds > ZoneConfig.getPower().getMinOdds() ? this.powerMax : this.powerMin;
            case POWER_LADDER:
                return this.powerLadder;
            case KENO:
                return this.keno;
            case KENO_LADDER:
                return this.kenoLadder;
            case BITCOIN3:
                return betOdds > ZoneConfig.getBitcoin3().getMinOdds() ? this.btc3Max : this.btc3Min;
            case BITCOIN5:
                return betOdds > ZoneConfig.getBitcoin5().getMinOdds() ? this.btc5Max : this.btc5Min;
            case CASINO:
                return this.casino;
            case SLOT:
                return this.slot;
            default:
                return this.rateZone;
        }
    }
}
