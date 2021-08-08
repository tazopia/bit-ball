package spoon.payment.domain;

public enum MoneyCode {

    ADD(1100, "관리자 증액"),
    REMOVE(1200, "관리자 차감"),

    DEPOSIT(2100, "충전"),
    DEPOSIT_ROLLBACK(2200, "충전취소"),

    WITHDRAW(3100, "환전"),
    WITHDRAW_ROLLBACK(3200, "환전취소"),

    BET_SPORTS(4100, "스포츠베팅"),
    BET_ZONE(4200, "실시간베팅"),
    BET_SPORTS_ROLLBACK(4300, "스포츠베팅 취소"),
    BET_ZONE_ROLLBACK(4400, "실시간베팅 취소"),
    BET_INPLAY(4500, "인플레이베팅"),
    BET_INPLAY_ROLLBACK(4600, "인플레이베팅 취소"),

    ROLL_SPORTS(4710, "스포츠롤링"),
    ROLL_ZONE(4720, "실시간롤링"),
    ROLL_CASINO(4730, "카지노롤링"),
    ROLL_SPORTS_ROLLBACK(4740, "스포츠롤링 취소"),
    ROLL_ZONE_ROLLBACK(4750, "실시간롤링 취소"),

    WIN(5100, "게임적중"),
    WIN_ROLLBACK(5200, "적중롤백"),

    WIN_INPLAY(5300, "인플레이적중"),
    WIN_INPLAY_ROLLBACK(5400, "인플레이적중롤백"),

    EXCHANGE(6100, "포인트전환"),

    GATE_IN(7100, "게이트 받음"),
    GATE_OUT(7200, "게이트 보냄"),

    SUN_IN(8100, "카지노 받음"),
    SUN_OUT(8200, "카지노 보냄"),

    CASINO_BET(9100, "카지노베팅"),
    CASINO_WIN(9200, "카지노적중"),
    CASINO_REFUND(9300, "카지노환금"),
    CASINO_CANCEL(9400, "카지노취소"),

    EVO_IN(8300, "카지노 받음"),
    EVO_OUT(8400, "카지노 보냄"),

    UNKNOWN(99999, "알수없음");

    private int value;
    private String name;

    MoneyCode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 입출금에 해당하는 코드를 가져온다.
     */
    public static MoneyCode[] getBankingCode() {
        return new MoneyCode[]{DEPOSIT, DEPOSIT_ROLLBACK, WITHDRAW, WITHDRAW_ROLLBACK};
    }

    /**
     * 베팅 및 적중에 해당하는 코드를 가져온다.
     */
    public static MoneyCode[] getBettingCode() {
        return new MoneyCode[]{BET_SPORTS, BET_ZONE, BET_SPORTS_ROLLBACK, BET_ZONE_ROLLBACK, WIN, WIN_ROLLBACK};
    }

    /**
     * 관리자 증차감에 해당하는 코드를 가져온다.
     */
    public static MoneyCode[] getAddRemoveCode() {
        return new MoneyCode[]{ADD, REMOVE};
    }

    /**
     * 롤링
     */
    public static MoneyCode[] getShareCode() {
        return new MoneyCode[]{ROLL_SPORTS, ROLL_ZONE, ROLL_SPORTS_ROLLBACK, ROLL_ZONE_ROLLBACK, ROLL_CASINO};
    }

    /**
     * 추가 사항이 생길때 마다 추가해 줘야 한다.
     */
    public static MoneyCode[] getEtcCode() {
        return new MoneyCode[]{EXCHANGE};
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static MoneyCode rollback(MoneyCode moneyCode) {
        switch (moneyCode) {
            case DEPOSIT:
                return DEPOSIT_ROLLBACK;
            case WITHDRAW:
                return WITHDRAW_ROLLBACK;
            case WIN:
                return WIN_ROLLBACK;
            case BET_SPORTS:
                return BET_SPORTS_ROLLBACK;
            case BET_ZONE:
                return BET_ZONE_ROLLBACK;
            case BET_INPLAY:
                return BET_INPLAY_ROLLBACK;
            case WIN_INPLAY:
                return WIN_INPLAY_ROLLBACK;
            case ROLL_SPORTS:
                return ROLL_SPORTS_ROLLBACK;
            case ROLL_ZONE:
                return ROLL_ZONE_ROLLBACK;
            default:
                throw new RuntimeException("롤백코드를 반환할 수 없습니다. - " + moneyCode);
        }
    }


}
