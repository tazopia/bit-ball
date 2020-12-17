package spoon.bot.balance.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bet.entity.BetItem;
import spoon.bet.service.BetGameService;
import spoon.bot.balance.domain.PolygonResult;
import spoon.bot.balance.entity.PolygonBalance;
import spoon.bot.balance.repository.PolygonBalanceRepository;
import spoon.bot.support.ZoneMaker;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.game.domain.MenuCode;
import spoon.gameZone.ZoneConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PolygonAladdinService {

    private BetGameService betGameService;

    private PolygonBalanceRepository polygonBalanceRepository;

    @Async
    public void balance() {

        double rate = Config.getGameConfig().getBalanceAladdinRate();
        if (rate == 0 || !Config.getGameConfig().isBalanceAladdin()) return;

        ZoneMaker zoneMaker = ZoneConfig.getAladdin().getZoneMaker();

        Date gameDate = zoneMaker.getGameDate();
        String sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");

        long calc;
        long odd = 0, even = 0, left = 0, right = 0, line3 = 0, line4 = 0;
        long bo = 0, be = 0, bl = 0, br = 0, b3 = 0, b4 = 0;
        long totalbet;
        List<String> betType = new ArrayList<>();
        boolean canbet = false;

        Iterable<BetItem> items = betGameService.getBalanceBet(MenuCode.ALADDIN, sdate);

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
        calc = (long)((odd - even) * rate / 100);
        if (calc >= 5000) {
            bo = calc;
            betType.add("홀");
            canbet = true;
        }
        calc = (long) ((even - odd) * rate / 100);
        if (calc >= 5000) {
            be = calc;
            canbet = true;
            betType.add("짝");
        }

        // 좌우
        calc = (long) ((left - right) * rate / 100);
        if (calc >= 5000) {
            bl = calc;
            canbet = true;
            betType.add("좌");
        }
        calc = (long) ((right - left) * rate / 100);
        if (calc >= 5000) {
            br = calc;
            canbet = true;
            betType.add("우");
        }

        // 3줄4줄
        calc = (long) ((line3 - line4) * rate / 100);
        if (calc >= 5000) {
            b3 = calc;
            canbet = true;
            betType.add("3줄");
        }
        calc = (long) ((line4 - line3) * rate / 100);
        if (calc >= 5000) {
            b4 = calc;
            canbet = true;
            betType.add("4줄");
        }

        if (canbet) {
            String betDate = zoneMaker.getRound() == 480 ? DateUtils.format(DateUtils.beforeDays(1, gameDate), "yyyy-MM-dd")
                    : DateUtils.format(gameDate, "yyyy-MM-dd");
            String round = String.format("%03d", zoneMaker.getRound());
            String url = Config.getSysConfig().getZone().getBalanceUrl() + "/bet/halfladder?auth=" + Config.getGameConfig().getBalancePolygon() +
                    "&gdate=" + betDate + "&times=" + round +
                    "&bo=" + resultValue(bo) +
                    "&be=" + resultValue(be) +
                    "&bl=" + resultValue(bl) +
                    "&br=" + resultValue(br) +
                    "&b3=" + resultValue(b3) +
                    "&b4=" + resultValue(b4);
            log.info("[알라딘] 단 : " + canbet + " / bo - " + bo + ", be - " + be + ", bl - " + bl + ", br - " + br + ", b3 - " + b3 + ", b4 - " + b4);
            String gameType = "단폴";
            totalbet = bo + be + bl + br + b3 + b4;
            sendQuery(url, betDate, round, gameType, String.join(", ", betType), totalbet);
        }

    }

    private void sendQuery(String param, String betDate, String round, String gameType, String betType, long price) {
        String json = HttpParsing.getJson(param);
        if (json == null) return;
        PolygonResult result = JsonUtils.toModel(json, PolygonResult.class);
        if (result == null) return;
        PolygonBalance b = new PolygonBalance();
        b.setGame("알라딘");
        b.setGameDate(betDate);
        b.setRound(round);
        b.setGameType(gameType);
        b.setBetType(betType);
        b.setPrice(price);
        b.setRegDate(new Date());
        b.setMessage(result.getMessage());
        polygonBalanceRepository.saveAndFlush(b);
    }

    private String resultValue(long value) {
        return value < 0 ? "" : String.valueOf(value);
    }
}
