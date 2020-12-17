package spoon.game.domain;

public enum GameResult {

    // 스포츠
    READY(0, "대기"),
    HOME(10, "홈승"),
    AWAY(20, "원정승"),
    DRAW(30, "무승"),
    UNDER(110, "언더"),
    OVER(120, "오버"),
    HIT(900, "적특"),
    DRAW_HIT(910, "타이"),
    CANCEL(920, "취소"),
    DENY(930, "관리자 취소"), // 관리자 취소
    NONE(990, "적중없음"),
    UNKNOWN(999, "알수없음");


    private int value;
    private String name;

    GameResult(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
