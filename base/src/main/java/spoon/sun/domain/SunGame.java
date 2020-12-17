package spoon.sun.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SunGame {

    PLAY_N_GO("PLAY'N GO", 1, SunGroup.SLOT, "PLAYNGO.png"),
    PRAGMATIC("PRAGMATIC", 2, SunGroup.SLOT, "PRAGMATIC.png"),
    SKYWIND("SKYWIND", 3, SunGroup.SLOT, "SKYWIND.png"),
    YGG("YGG", 4, SunGroup.SLOT, "YGG.png"),
    BOOONGO("BOOONGO", 5, SunGroup.SLOT, "BOOONGO.png"),
    GAMATRON("GAMATRON", 6, SunGroup.SLOT, "GAMATRON.png"),
    ALLWAYSPIN("ALLWAYSPIN", 7, SunGroup.SLOT, "ALLWAYSPIN.png"),
    MICROGAMING("MICROGAMING", 8, SunGroup.SLOT, "MICROGAMING.png"),
    QUICKSPIN("QUICKSPIN", 9, SunGroup.SLOT, "QUICKSPIN.png"),
    BETSOFT("BETSOFT", 10, SunGroup.SLOT, "BETSOFT.png"),
    PLAYSON("PLAYSON", 11, SunGroup.SLOT, "PLAYSON.png"),
    BLUEPRINT("BLUEPRINT", 12, SunGroup.SLOT, "BLUEPRINT.png"),
    HABANERO("HABANERO", 13, SunGroup.SLOT, "HABANERO.png"),
    ASIAGAMING("ASIAGAMING", 14, SunGroup.SLOT, "ASIAGAMING.png"),
    AMEBA("AMEBA", 15, SunGroup.SLOT, "AMEBA.png"),
    DREAMTECH("DREAMTECH", 16, SunGroup.SLOT, "DREAMTECH.png"),
    ELK("ELK", 17, SunGroup.SLOT, "ELK.png"),
    ISOFTBET("ISOFTBET", 18, SunGroup.SLOT, "ISOFTBET.png"),
    LEANDER("LEANDER", 19, SunGroup.SLOT, "LEANDER.png"),
    MAVERICK("MAVERICK", 20, SunGroup.SLOT, "MAVERICK.png"),
    NETENT("NETENT", 21, SunGroup.SLOT, "NETENT.png"),
    RTG("RTG", 22, SunGroup.SLOT, "RTG.png"),
    SPADEGAMING("SPADEGAMING", 23, SunGroup.SLOT, "SPADEGAMING.png"),

    EVOLUTION_LIVE("애볼루션", 24, SunGroup.LIVE, "EVOLUTION.png"),
    PRAGMATIC_LIVE("프라그마틱 플레이", 25, SunGroup.LIVE, "PRAGMATIC.png"),
    MICRO_LIVE("마이크로 게이밍", 26, SunGroup.LIVE, "MICROGAMING.png"),
    ASIAGAMING_LIVE("아시아 게이밍", 27, SunGroup.LIVE, "ASIAGAMING.png"),
    VIVO_LIVE("비보 게이밍", 28, SunGroup.LIVE, "VIVOGAMING.png"),
    WM_LIVE("WM 게이밍", 29, SunGroup.LIVE, "WMGAMING.png"),
    ALLBET_LIVE("올벳", 30, SunGroup.LIVE, "ALLBET.png"),
    BBIN_LIVE("BBIN", 31, SunGroup.LIVE, "BBIN.png"),
    SEXY_LIVE("섹시 게이밍", 32, SunGroup.LIVE, "SEXYGAMING.png"),
    OG_LIVE("오리엔탈 게임", 33, SunGroup.LIVE, "ORIENTALGAME.png");

    private final String value;
    private final int gameNo;
    private final SunGroup group;
    private final String flag;

    public static SunGame numOf(int gameNo) {
        for (SunGame g : SunGame.values()) {
            if (g.gameNo == gameNo) return g;
        }
        throw new IllegalArgumentException(gameNo + " 게임을 찾을수 없습니다.");
    }

    public enum SunGroup {
        SLOT,
        LIVE
    }
}
