package spoon.banking.domain;

public enum BankingCode {

    IN(100, "입금"),
    OUT(10000, "출금");

    private int value;
    private String name;

    BankingCode(int value, String name) {
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
