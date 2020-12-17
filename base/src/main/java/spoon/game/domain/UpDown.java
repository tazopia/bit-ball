package spoon.game.domain;

public enum UpDown {
    UP(1), DOWN(-1), KEEP(0);

    private int value;

    UpDown(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
