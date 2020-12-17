package spoon.game.domain;

public enum GameCode {

    MATCH(100, "승무패"),
    HANDICAP(200, "핸디캡"),
    OVER_UNDER(300, "오버언더"),

    ZONE(1000, "게임존");

    private int value;

    private String name;

    GameCode(int value, String name) {
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
