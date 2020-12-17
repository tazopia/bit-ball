package spoon.sun.domain;

import lombok.Getter;

@Getter
public class SunBalance {

    private final String gameName;

    private final int gameNo;

    private final SunGame.SunGroup group;

    private final long money;

    private final String errorMsg;

    private final String code;

    private final String flag;

    private SunBalance(SunGame game, SunResponseDto.Balance balance) {
        this.gameName = game.getValue();
        this.gameNo = game.getGameNo();
        this.group = game.getGroup();
        this.money = balance.getMoney();
        this.errorMsg = balance.getErrorMsg();
        this.code = balance.getRespCode();
        this.flag = game.getFlag();
    }

    public static SunBalance of(SunGame game, SunResponseDto.Balance balance) {
        return new SunBalance(game, balance);
    }
}
