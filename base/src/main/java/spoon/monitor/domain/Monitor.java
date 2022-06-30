package spoon.monitor.domain;

import lombok.Data;
import spoon.gameZone.ZoneConfig;

@Data
public class Monitor {

    private long money;

    private long point;

    private long in;

    private long out;

    // 충전
    private long deposit;

    private long alarmDeposit;

    // 환전
    private long withdraw;

    private long alarmWithdraw;

    // 고객응대
    private long qna;

    private long alarmQna;

    // 블랙회원 베팅
    private long black;

    // 신규회원
    private long member;

    // 게시판
    private long board;

    // 결과처리
    private long cross;

    private long special;

    private long live;

    // 정산
    private long sports;
    private long sportsEnd;

    private long ladder;
    private long ladderEnd;

    private long dari;
    private long dariEnd;

    private long snail;
    private long snailEnd;

    private long newSnail;
    private long newSnailEnd;

    private long power;
    private long powerEnd;

    private long powerLadder;
    private long powerLadderEnd;

    private long keno;
    private long kenoEnd;

    private long kenoLadder;
    private long kenoLadderEnd;

    private long aladdin;
    private long aladdinEnd;

    private long lowhi;
    private long lowhiEnd;

    private long oddeven;
    private long oddevenEnd;

    private long baccarat;
    private long baccaratEnd;

    private long soccer;
    private long soccerEnd;

    private long dog;
    private long dogEnd;

    private long luck;
    private long luckEnd;

    private long crownOddeven;
    private long crownOddevenEnd;

    private long crownBaccarat;
    private long crownBaccaratEnd;

    private long crownSutda;
    private long crownSutdaEnd;

    // inplay
    private long inplay;
    private long inplayEnd;

    // sun
    private long sun;
    private long sunEnd;

    // 카지노
    private long casino;
    private long casinoEnd;
    private long casinoBalance;

    // 비트코인
    private long bitcoin1;
    private long bitcoin1End;

    private long bitcoin3;
    private long bitcoin3End;

    private long bitcoin5;
    private long bitcoin5End;

    // eos
    private long eos1;
    private long eos1End;
    private long eos2;
    private long eos2End;
    private long eos3;
    private long eos3End;
    private long eos4;
    private long eos4End;
    private long eos5;
    private long eos5End;

    // 게임존
    public long getLadderResult() {
        return ZoneConfig.getLadder().getResult();
    }

    public long getDariResult() {
        return ZoneConfig.getDari().getResult();
    }

    public long getSnailResult() {
        return ZoneConfig.getSnail().getResult();
    }

    public long getNewSnailResult() {
        return ZoneConfig.getNewSnail().getResult();
    }

    public long getPowerResult() {
        return ZoneConfig.getPower().getResult();
    }

    public long getPowerLadderResult() {
        return ZoneConfig.getPowerLadder().getResult();
    }

    public long getKenoLadderResult() {
        return ZoneConfig.getKenoLadder().getResult();
    }

    public long getKenoResult() {
        return ZoneConfig.getKeno().getResult();
    }

    public long getAladdinResult() {
        return ZoneConfig.getAladdin().getResult();
    }

    public long getLowhiResult() {
        return ZoneConfig.getLowhi().getResult();
    }

    public long getOddevenResult() {
        return ZoneConfig.getOddeven().getResult();
    }

    public long getBaccaratResult() {
        return ZoneConfig.getBaccarat().getResult();
    }

    public long getSoccerResult() {
        return ZoneConfig.getSoccer().getResult();
    }

    public long getDogResult() {
        return ZoneConfig.getDog().getResult();
    }

    public long getLuckResult() {
        return ZoneConfig.getLuck().getResult();
    }

    public long getCrownOddevenResult() {
        return ZoneConfig.getCrownOddeven().getResult();
    }

    public long getCrownBaccaratResult() {
        return ZoneConfig.getCrownBaccarat().getResult();
    }

    public long getBitcoin1Result() {
        return ZoneConfig.getBitcoin1().getResult();
    }

    public long getBitcoin3Result() {
        return ZoneConfig.getBitcoin3().getResult();
    }

    public long getBitcoin5Result() {
        return ZoneConfig.getBitcoin5().getResult();
    }


    public long getEos1Result() {
        return ZoneConfig.getEos1().getResult();
    }

    public long getEos2Result() {
        return ZoneConfig.getEos2().getResult();
    }

    public long getEos3Result() {
        return ZoneConfig.getEos3().getResult();
    }

    public long getEos4Result() {
        return ZoneConfig.getEos4().getResult();
    }

    public long getEos5Result() {
        return ZoneConfig.getEos5().getResult();
    }
}
