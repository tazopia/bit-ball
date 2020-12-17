package spoon.bot.sports.ferrari.domain;

import lombok.Data;

@Data
public class BotFerrari {

    // 배당 고유번호
    private long sid;

    // 경기 고유번호
    private long gid;

    // 배당 제공 업체
    private String pv;

    // 종목
    private String sports;

    // 리그
    private String league;

    // 리그기
    private String flag;

    // 경기 시작시간: yyyy-MM-dd HH:mm:ss
    private String sdate;

    private String team1;

    private String team2;

    // 메뉴 : match, handicap, special, live
    private String menu;

    // 베팅타입 : 12, 1x2, ah, ou
    private String type;

    private double home;

    private double draw;

    private double away;

    private String special;

    private int score1;

    private int score2;

    // S: 대기, F: 종료, C: 취소, D: 삭제
    private String state;

    // 베팅 가능한지 여부
    private boolean bet;

    // 최종 업데이트 millisecond
    private long ms;

}
