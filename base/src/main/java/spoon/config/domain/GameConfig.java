package spoon.config.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameConfig {

    private boolean autoParsing = true;
    private boolean autoUpdate = true;
    private boolean autoClosing;

    private boolean endGame;

    private boolean balanceLadder;
    private boolean balanceDari;
    private boolean balancePower;
    private boolean balanceAladdin;
    private boolean balanceLowhi;
    private double balanceLadderRate;
    private double balanceDariRate;
    private double balancePowerRate;
    private double balanceAladdinRate;
    private double balanceLowhiRate;

    private String balancePolygon;

    private String balanceGateId;
    private String balanceGateKey;

    private double oddsDefault = 1.8;
    private double oddsUp = 0.05;
    private double oddsDown = 0.05;

    // 보너스 배당을 받기위한 최소 배당
    private boolean enabledMinOdds;
    private double minOdds;

    // 보너스 배당 설정
    private boolean[] bonus = new boolean[3]; // 0:클로스, 1:스페셜, 2:라이브
    private double[] bonusOdds = {0, 1, 1, 1.03, 1.03, 1.05, 1.05, 1.08, 1.08, 1.1, 1.1};
    private double bonusOne = 0.1;

    private double oddsUpDownMatch = 100;
    private double oddsPlusMatch = 0;

    private double oddsUpDownHandicap = 100;
    private double oddsUpDownOverUnder = 100;
    private double oddsUpDownHandicapSpecial = 100;
    private double oddsUpDownOverUnderSpecial = 100;

    private double oddsPlusHandicap = 0;
    private double oddsPlusOverUnder = 0;
    private double oddsPlusHandicapSpecial = 0;
    private double oddsPlusOverUnderSpecial = 0;

    // 경기 접기 안 접기
    private boolean moreCross = true;
    private boolean moreSpecial = true;
    private boolean moreLive = true;

    // 베팅 시작 및 취소 설정
    private int sportsTime = 60;
    private int sportsMaxFolder = 10;
    private int sportsBetCnt = 2;
    private double sportsMaxOdds;

    // 베팅 취소
    private int cancelBetTime = 60;
    private int cancelGameTime = 1;
    private int cancelMax = 10;

    // 조합베팅 금지
    private CbBet cbDefault = new CbBet(); // 기본설정
    private CbBet cbSoccer = new CbBet(); // 축구
    private CbBet cbBasketball = new CbBet(); // 농구
    private CbBet cbVolleyball = new CbBet(); // 배구
    private CbBet cbHockey = new CbBet(); // 아이스하키

    // 미적중 포인트 지급
    private int noHitFolder = 2; // 미적중 포인트 지급 폴더
    private int recommFolder = 0; // 추천인 지급 폴더
    private boolean recommType;
    private boolean recommPayment = true;
    private double[] noHitSportsOdds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] noHitSportsRecommOdds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] noHitZoneOdds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] noHitZoneRecommOdds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // 크로스
    private int[] crossWin = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};
    private int[] crossMax = {0, 3000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 500000, 300000, 300000};
    private int[] crossMin = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};
    private int[] crossMark = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 0, 0, 0, 0, 0};

    private boolean crossOne;
    private int[] crossOneWin = {0, 8000000, 5000000, 3000000, 3000000, 3000000, 0, 0, 0, 0, 0};
    private int[] crossOneMax = {0, 1000000, 1000000, 1000000, 500000, 500000, 0, 0, 0, 0, 0};
    private int[] crossOneMin = {0, 5000, 5000, 5000, 5000, 5000, 0, 0, 0, 0, 0};

    // 스페셜
    private int[] specialWin = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};
    private int[] specialMax = {0, 3000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 500000, 300000, 300000};
    private int[] specialMin = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};
    private int[] specialMark = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 0, 0, 0, 0, 0};

    private boolean specialOne;
    private int[] specialOneWin = {0, 8000000, 5000000, 3000000, 3000000, 3000000, 0, 0, 0, 0, 0};
    private int[] specialOneMax = {0, 1000000, 1000000, 1000000, 500000, 500000, 0, 0, 0, 0, 0};
    private int[] specialOneMin = {0, 5000, 5000, 5000, 5000, 5000, 0, 0, 0, 0, 0};

    // 라이브
    private int[] liveWin = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};
    private int[] liveMax = {0, 3000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 500000, 300000, 300000};
    private int[] liveMin = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};
    private int[] liveMark = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 0, 0, 0, 0, 0};

    private boolean liveOne;
    private int[] liveOneWin = {0, 8000000, 5000000, 3000000, 3000000, 3000000, 0, 0, 0, 0, 0};
    private int[] liveOneMax = {0, 1000000, 1000000, 1000000, 500000, 500000, 0, 0, 0, 0, 0};
    private int[] liveOneMin = {0, 5000, 5000, 5000, 5000, 5000, 0, 0, 0, 0, 0};

    // 인플레이
    private int[] inplayWin = {0, 5000000, 4000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000, 3000000};
    private int[] inplayMax = {0, 3000000, 1000000, 1000000, 1000000, 1000000, 1000000, 1000000, 500000, 300000, 300000};
    private int[] inplayMin = {0, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};

}
