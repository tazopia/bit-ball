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
import spoon.bot.support.ZoneMaker;
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
public class GateLadderService {

    private BetGameService betGameService;

    private PolygonBalanceRepository polygonBalanceRepository;

    @Async
    public void balance() {

        double rate = Config.getGameConfig().getBalanceLadderRate();
        if (rate == 0 || !Config.getGameConfig().isBalanceLadder()) return;

        ZoneMaker zoneMaker = ZoneConfig.getLadder().getZoneMaker();

        Date gameDate = zoneMaker.getGameDate();
        String sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");

        long calc;
        long odd = 0, even = 0, left = 0, right = 0, line3 = 0, line4 = 0;
        long totalOddeven = 0, totalStart = 0, totalLine = 0;
        int posOddeven = 0, posStart = 0, posLine = 0;
        boolean canOddeven = false, canStart = false, canLine = false;
        String round = String.format("%03d", zoneMaker.getRound());

        Iterable<BetItem> items = betGameService.getBalanceBet(MenuCode.LADDER, sdate);

        for (BetItem item : items) {
            switch (item.getSpecial()) {
                case "oddeven":
                    if ("home".equals(item.getBetTeam())) {
                        odd += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        even += item.getBetMoney();
                    }
                    break;
                case "start":
                    if ("home".equals(item.getBetTeam())) {
                        left += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        right += item.getBetMoney();
                    }
                    break;
                case "line":
                    if ("home".equals(item.getBetTeam())) {
                        line3 += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        line4 += item.getBetMoney();
                    }
                    break;
            }
        }

        // 홀짝
        calc = (long) ((odd - even) * rate / 100);
        if (calc >= 5000) {
            totalOddeven = calc;
            posOddeven = 1;
            canOddeven = true;
        }
        calc = (long) ((even - odd) * rate / 100);
        if (calc >= 5000) {
            totalOddeven = calc;
            posOddeven = 2;
            canOddeven = true;
        }

        // 좌우
        calc = (long) ((left - right) * rate / 100);
        if (calc >= 5000) {
            totalStart = calc;
            posStart = 1;
            canStart = true;
        }
        calc = (long) ((right - left) * rate / 100);
        if (calc >= 5000) {
            totalStart = calc;
            posStart = 2;
            canStart = true;
        }

        // 3줄4줄
        calc = (long) ((line3 - line4) * rate / 100);
        if (calc >= 5000) {
            totalLine = calc;
            posLine = 1;
            canLine = true;
        }
        calc = (long) ((line4 - line3) * rate / 100);
        if (calc >= 5000) {
            totalLine = calc;
            posLine = 2;
            canLine = true;
        }

        if (canOddeven) {
            String url = Config.getSysConfig().getZone().getBalanceGateLadderUrl()
                    + "?USER_ID=" + Config.getGameConfig().getBalanceGateId()
                    + "&USER_KEY=" + Config.getGameConfig().getBalanceGateKey()
                    + "&BET_TYPE=1&BET_SELECT=" + posOddeven
                    + "&BET_AMOUNT=" + totalOddeven
                    + "&GS_SEQ=1" + sdate;
            sendQuery(url, sdate, round, "홀짝", posOddeven == 1 ? "홀" : "짝", totalOddeven);
        }

        if (canStart) {
            String url = Config.getSysConfig().getZone().getBalanceGateLadderUrl()
                    + "?USER_ID=" + Config.getGameConfig().getBalanceGateId()
                    + "&USER_KEY=" + Config.getGameConfig().getBalanceGateKey()
                    + "&BET_TYPE=2&BET_SELECT=" + posStart
                    + "&BET_AMOUNT=" + totalStart
                    + "&GS_SEQ=2" + sdate;
            sendQuery(url, sdate, round, "좌우", posStart == 1 ? "좌" : "우", totalStart);
        }

        if (canLine) {
            String url = Config.getSysConfig().getZone().getBalanceGateLadderUrl()
                    + "?USER_ID=" + Config.getGameConfig().getBalanceGateId()
                    + "&USER_KEY=" + Config.getGameConfig().getBalanceGateKey()
                    + "&BET_TYPE=3&BET_SELECT=" + posLine
                    + "&BET_AMOUNT=" + totalLine
                    + "&GS_SEQ=3" + sdate;
            sendQuery(url, sdate, round, "3줄4줄", posLine == 1 ? "3줄" : "4줄", totalLine);
        }

    }

    private void sendQuery(String param, String betDate, String round, String gameType, String betType, long price) {
        String json = HttpParsing.getJson(param);
        if (json == null) return;
        GateResult result = JsonUtils.toModel(json, GateResult.class);
        if (result == null) return;
        PolygonBalance b = new PolygonBalance();
        b.setGame("사다리");
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
