package spoon.payment.domain;

public enum PointCode {

    ADD(1100, "관리자 증액"),
    REMOVE(1200, "관리자 차감"),

    DEPOSIT(2100, "충전"),
    DEPOSIT_ROLLBACK(2200, "충전취소"),
    SALE(2300, "총판 배당금"),
    WITHDRAW(2400, "총판 환전"),
    CHANGE(2500, "포인트 지급"),

    LOSE(3100, "미적중 포인트"),
    LOSE_ROLLBACK(3200, "미적중 포인트 롤백"),
    RECOMM(3300, "추천인 미적중 포인트"),
    RECOMM_ROLLBACK(3400, "추천인 미적중 롤백"),

    JOIN(4100, "가입축하 포인트"),
    LOGIN(4200, "첫로그인 포인트"),
    BOARD(4300, "글작성 포인트"),
    COMMENT(4400, "댓글 포인트"),
    EVENT(4500, "이벤트 포인트"),

    EVO_CASINO(4600, "카지노 롤링"),

    EXCHANGE(5100, "포인트전환"),

    UNKNOWN(9999, "알수없음");

    private int value;
    private String name;

    PointCode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PointCode[] getBankingCode() {
        return new PointCode[]{DEPOSIT, DEPOSIT_ROLLBACK};
    }

    public static PointCode[] getBettingCode() {
        return new PointCode[]{LOSE, LOSE_ROLLBACK, RECOMM, RECOMM_ROLLBACK};
    }

    public static PointCode[] getAddRemoveCode() {
        return new PointCode[]{ADD, REMOVE, SALE};
    }

    public static PointCode[] getExchangeCode() {
        return new PointCode[] {EXCHANGE};
    }

    public static PointCode[] getSellerCode() {
        return new PointCode[] {WITHDRAW, CHANGE};
    }

    /**
     * 추가 사항이 생길때 마다 추가해 줘야 한다.
     */
    public static PointCode[] getEtcCode() {
        return new PointCode[]{LOGIN, BOARD, COMMENT, EVENT};
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static PointCode rollback(PointCode pointCode) {
        switch (pointCode) {
            case DEPOSIT:
                return DEPOSIT_ROLLBACK;
            case LOSE:
                return LOSE_ROLLBACK;
            case RECOMM:
                return RECOMM_ROLLBACK;
            default:
                throw new RuntimeException("롤백코드를 반환할 수 없습니다. - " + pointCode);
        }
    }


}
