package spoon.casino.evo.domain;

import lombok.Data;

@Data
public class CasinoEvoResult {

    private int returnCode = -1;

    private String message;

    private long money;

    public static CasinoEvoResult error(String message) {
        CasinoEvoResult result = new CasinoEvoResult();
        result.message = message;
        return result;
    }

    public static CasinoEvoResult success() {
        CasinoEvoResult result = new CasinoEvoResult();
        result.returnCode = 1;
        return result;
    }

    public static CasinoEvoResult success(long money) {
        CasinoEvoResult result = new CasinoEvoResult();
        result.money = money;
        result.returnCode = 1;
        return result;
    }
}
