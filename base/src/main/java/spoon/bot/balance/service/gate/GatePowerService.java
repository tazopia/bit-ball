package spoon.bot.balance.service.gate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bet.entity.BetItem;
import spoon.bet.service.BetGameService;
import spoon.bot.balance.domain.GateResult;
import spoon.bot.balance.entity.PolygonBalance;
import spoon.bot.balance.repository.PolygonBalanceRepository;
import spoon.bot.support.PowerMaker;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.game.domain.MenuCode;
import spoon.gameZone.ZoneConfig;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class GatePowerService {

    private BetGameService betGameService;

    private PolygonBalanceRepository polygonBalanceRepository;

    @Async
    public void balance() {

        double rate = Config.getGameConfig().getBalancePowerRate();
        if (rate == 0 || !Config.getGameConfig().isBalancePower()) return;

        PowerMaker powerMaker = ZoneConfig.getPower().getPowerMaker();
        long times = powerMaker.getTimes() + 1;

        Date gameDate = powerMaker.getGameDate(times);
        String sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");

        long calc;
        long pOdd = 0, pEven = 0, odd = 0, even = 0, over = 0, under = 0;
        long totalPoe = 0, totalOe = 0, totalOu = 0;
        int posPoe = 0, posOe = 0, posOu = 0;
        boolean canPoe = false, canOe = false, canOu = false;
        String round = String.format("%03d", times);

        Iterable<BetItem> items = betGameService.getBalanceBet(MenuCode.POWER, sdate);

        for (BetItem item : items) {
            switch (item.getSpecial()) {
                case "pb_oddeven":
                    if ("home".equals(item.getBetTeam())) {
                        pOdd += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        pEven += item.getBetMoney();
                    }
                    break;
                case "oddeven":
                    if ("home".equals(item.getBetTeam())) {
                        odd += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        even += item.getBetMoney();
                    }
                    break;
                case "pb_overunder":
                    if ("home".equals(item.getBetTeam())) {
                        over += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        under += item.getBetMoney();
                    }
                    break;
            }
        }

        // 파워볼 홀짝
        calc = (long) ((pOdd - pEven) * rate / 100);
        if (calc >= 5000) {
            totalPoe = calc;
            posPoe = 1;
            canPoe = true;
        }
        calc = (long) ((pEven - pOdd) * rate / 100);
        if (calc >= 5000) {
            totalPoe = calc;
            posPoe = 2;
            canPoe = true;
        }

        // 일반볼 홀짝
        calc = (long) ((odd - even) * rate / 100);
        if (calc >= 5000) {
            totalOe = calc;
            posOe = 1;
            canOe = true;
        }
        calc = (long) ((even - odd) * rate / 100);
        if (calc >= 5000) {
            totalOe = calc;
            posOe = 2;
            canOe = true;
        }

        // 파워볼 오버언더
        calc = (long) ((over - under) * rate / 100);
        if (calc >= 5000) {
            totalOu = calc;
            posOu = 1;
            canOu = true;
        }
        calc = (long) ((under - over) * rate / 100);
        if (calc >= 5000) {
            totalOu = calc;
            posOu = 2;
            canOu = true;
        }

        if (canPoe) {
            String url = Config.getSysConfig().getZone().getBalanceGatePowerUrl()
                    + "?USER_ID=" + Config.getGameConfig().getBalanceGateId()
                    + "&USER_KEY=" + Config.getGameConfig().getBalanceGateKey()
                    + "&BET_TYPE=1&BET_SELECT=" + posPoe
                    + "&BET_AMOUNT=" + totalPoe
                    + "&GS_SEQ=1" + sdate;
            sendQuery(url, sdate, round, "파워볼 홀짝", posPoe == 1 ? "홀" : "짝", totalPoe);
        }

        if (canOe) {
            String url = Config.getSysConfig().getZone().getBalanceGatePowerUrl()
                    + "?USER_ID=" + Config.getGameConfig().getBalanceGateId()
                    + "&USER_KEY=" + Config.getGameConfig().getBalanceGateKey()
                    + "&BET_TYPE=2&BET_SELECT=" + posOe
                    + "&BET_AMOUNT=" + totalOe
                    + "&GS_SEQ=2" + sdate;
            sendQuery(url, sdate, round, "일반볼 홀짝", posOe == 1 ? "홀" : "짝", totalOe);
        }

        if (canOu) {
            String url = Config.getSysConfig().getZone().getBalanceGatePowerUrl()
                    + "?USER_ID=" + Config.getGameConfig().getBalanceGateId()
                    + "&USER_KEY=" + Config.getGameConfig().getBalanceGateKey()
                    + "&BET_TYPE=3&BET_SELECT=" + posOu
                    + "&BET_AMOUNT=" + totalOu
                    + "&GS_SEQ=3" + sdate;
            sendQuery(url, sdate, round, "파워볼 오버언더", posOu == 1 ? "오버" : "언더", totalOu);
        }

    }

    private void sendQuery(String param, String betDate, String round, String gameType, String betType, long price) {
        String json = HttpParsing.getJson(param);
        if (json == null) return;
        GateResult result = JsonUtils.toModel(json, GateResult.class);
        if (result == null) return;
        PolygonBalance b = new PolygonBalance();
        b.setGame("파워볼");
        b.setGameDate(betDate);
        b.setRound(round);
        b.setGameType(gameType);
        b.setBetType(betType);
        b.setPrice(price);
        b.setRegDate(new Date());
        b.setMessage(result.getMessage());
        polygonBalanceRepository.saveAndFlush(b);
    }

}
