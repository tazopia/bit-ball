package spoon.member.domain;

import lombok.Getter;
import spoon.common.utils.DateUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;

@Getter
public class UserBetInfo {

    private final int level = WebUtils.level();

    // 스포츠 낙첨시 유저
    private final int noHitFolder = Config.getGameConfig().getNoHitFolder();
    private final double noHitSportsOdds = Config.getGameConfig().getNoHitSportsOdds()[level];

    // 스포츠 낙첨시 추천인
    private final int recommFolder = Config.getGameConfig().getRecommFolder();
    private final boolean recommType = Config.getGameConfig().isRecommType(); // true 베팅시 베팅금액, false 낙첨시 베팅금액
    private final double noHitSportsRecommOdds = Config.getGameConfig().getNoHitSportsRecommOdds()[level];
    private final boolean recommPayment = Config.getGameConfig().isRecommPayment();

    // 가입첫충, 첫충, 매충,
    private final double joinRate = Config.getSiteConfig().getPoint().getJoinRate()[level];
    private final double firstRate = Config.getSiteConfig().getPoint().getFirstRate()[level];
    private final double everyRate = Config.getSiteConfig().getPoint().getEveryRate()[level];

    // 충전 이벤트 보너스
    private final boolean eventFirst = Config.getSiteConfig().getPoint().isEventFirst(); // true 첫충만, false 모든충전
    private final boolean eventPayment = Config.getSiteConfig().getPoint().isEventPayment();
    private final boolean weekEvent = Config.getSiteConfig().getPoint().getEvent()[DateUtils.week()];
    private final double weekEventRate = Config.getSiteConfig().getPoint().getEventRate()[DateUtils.week()];

    public String getFolder() {
        if (recommPayment && recommFolder > 0) {
            return recommFolder + "폴더 이상";
        }

        return "없음";
    }

    public double getOdds() {
        if (getFolder().equals("없음")) return 0;
        return noHitSportsOdds;
    }
}
