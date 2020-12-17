package spoon.bot.balance;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spoon.bot.balance.service.gate.GateDariService;
import spoon.bot.balance.service.gate.GateLadderService;
import spoon.bot.balance.service.gate.GatePowerService;
import spoon.config.domain.Config;

@Slf4j
@AllArgsConstructor
@Component
public class GateTask {

    private GateLadderService gateLadderService;

    private GateDariService gateDariService;

    private GatePowerService gatePowerService;

    /**
     * 사다리
     */
    @Scheduled(cron = "36 3/5 * * * ?")
    public void ladderBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceLadder()) {
            gateLadderService.balance();
        }
    }

    /**
     * 다리다리
     */
    @Scheduled(cron = "36 1/3 * * * ?")
    public void dariBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceDari()) {
            gateDariService.balance();
        }
    }

    /**
     * 파워볼
     */
    @Scheduled(cron = "55 1/5 * * * ?")
    public void powerBalance() {
        if (canBalance() && Config.getGameConfig().isBalanceAladdin()) {
            gatePowerService.balance();
        }
    }


    private boolean canBalance() {
        return Config.getSysConfig().getZone().isBalance()
                && "gate".equals(Config.getSysConfig().getZone().getBalanceType());
    }

}
