package spoon.bot.balance;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.balance.service.PolygonAladdinService;
import spoon.bot.balance.service.PolygonDariService;
import spoon.bot.balance.service.PolygonLadderService;
import spoon.bot.balance.service.PolygonLowhiService;
import spoon.config.domain.Config;

@Slf4j
@AllArgsConstructor
@Component
public class PolygonTask {

    private PolygonLadderService polygonLadderService;

    private PolygonDariService polygonDariService;

    private PolygonAladdinService polygonAladdinService;

    private PolygonLowhiService polygonLowhiService;

    /**
     * 사다리
     */
    @Scheduled(cron = "37 3/5 * * * ?")
    public void ladderBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceLadder()) {
            polygonLadderService.balance();
        }
    }

    /**
     * 다리다리
     */
    @Scheduled(cron = "52 1/3 * * * ?")
    public void dariBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceDari()) {
            polygonDariService.balance();
        }
    }

    /**
     * 알라딘
     */
    @Scheduled(cron = "42 1/3 * * * ?")
    public void aladdinBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceAladdin()) {
            polygonAladdinService.balance();
        }
    }

    /**
     * 로하이
     */
    @Scheduled(cron = "43 1/3 * * * ?")
    public void lowhiBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceLowhi()) {
            polygonLowhiService.balance();
        }
    }

    private boolean canBalance() {
        return Config.getSysConfig().getZone().isBalance()
                && "polygon".equals(Config.getSysConfig().getZone().getBalanceType());
    }

}
