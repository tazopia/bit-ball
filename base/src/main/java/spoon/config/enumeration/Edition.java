package spoon.config.enumeration;

public enum Edition {

    BASIC(100, "베이직"),
    GOLD(200, "골드"),
    DIAMOND(300, "다이아몬드");

    private int value;

    private String name;

    Edition(int value, String name) {
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
