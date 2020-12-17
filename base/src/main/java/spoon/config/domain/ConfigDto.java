package spoon.config.domain;

import lombok.Data;

public class ConfigDto {

    @Data
    public static class Join {
        private boolean requiredRecommend = Config.getSiteConfig().getJoin().isRequiredRecommend();
        private boolean checkRecommend = Config.getSiteConfig().getJoin().isCheckRecommend();
        private boolean duplicatePhone = Config.getSiteConfig().getJoin().isDuplicatePhone();
        private boolean duplicateAccount = Config.getSiteConfig().getJoin().isDuplicateAccount();
    }

    @Data
    public static class Game {
        private long money;
        private int win;
        private int max;
        private int min;
        private int mark;
        private boolean one;
        private int oneWin;
        private int oneMax;
        private int oneMin;

        private double minOdd;

        private int sportsMaxFolder = Config.getGameConfig().getSportsMaxFolder();
        private int sportsBetCnt = Config.getGameConfig().getSportsBetCnt();
        private double sportsMaxOdds = Config.getGameConfig().getSportsMaxOdds();
        private double[] bonusOdds = new double[11];
        private double sportsTime = Config.getGameConfig().getSportsTime();
        private double bonusOne = Config.getGameConfig().getBonusOne();

        private CbBet cbDefault = Config.getGameConfig().getCbDefault(); // 기본설정
        private CbBet cbSoccer = Config.getGameConfig().getCbSoccer(); // 축구
        private CbBet cbBasketball = Config.getGameConfig().getCbBasketball(); // 농구
        private CbBet cbVolleyball = Config.getGameConfig().getCbVolleyball(); // 배구
        private CbBet cbHockey = Config.getGameConfig().getCbHockey(); // 아이스하키

    }

}
