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
public class PolygonLowhiService {

    private BetGameService betGameService;

    private PolygonBalanceRepository polygonBalanceRepository;

    @Async
    public void balance() {

        double rate = Config.getGameConfig().getBalanceLowhiRate();
        if (rate == 0 || !Config.getGameConfig().isBalanceLowhi()) return;

        ZoneMaker zoneMaker = ZoneConfig.getLowhi().getZoneMaker();

        Date gameDate = zoneMaker.getGameDate();
        String sdate = DateUtils.format(gameDate, "yyyyMMddHHmm");

        long calc;
        long odd = 0, even = 0, low = 0, hi = 0;
        long bo = 0, be = 0, bl = 0, bh = 0;
        long totalbet;
        List<String> betType = new ArrayList<>();
        boolean canbet = false;

        Iterable<BetItem> items = betGameService.getBalanceBet(MenuCode.LOWHI, sdate);

        for (BetItem item : items) {
            switch (item.getSpecial()) {
                case "oddeven":
                    if ("home".equals(item.getBetTeam())) {
                        odd += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        even += item.getBetMoney();
                    }
                    break;
                case "lowhi":
                    if ("home".equals(item.getBetTeam())) {
                        low += item.getBetMoney();
                    } else if ("away".equals(item.getBetTeam())) {
                        hi += item.getBetMoney();
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

        // 로하이
        calc = (long) ((low - hi) * rate / 100);
        if (calc >= 5000) {
            bl = calc;
            canbet = true;
            betType.add("로우");
        }
        calc = (long) ((hi - low) * rate / 100);
        if (calc >= 5000) {
            bh = calc;
            canbet = true;
            betType.add("하이");
        }

        if (canbet) {
            String betDate = zoneMaker.getRound() == 480 ? DateUtils.format(DateUtils.beforeDays(1, gameDate), "yyyy-MM-dd")
                    : DateUtils.format(gameDate, "yyyy-MM-dd");
            String round = String.format("%03d", zoneMaker.getRound());
            String url = Config.getSysConfig().getZone().getBalanceUrl() + "/bet/halflowhigh?auth=" + Config.getGameConfig().getBalancePolygon() +
                    "&gdate=" + betDate + "&times=" + round +
                    "&bo=" + resultValue(bo) +
                    "&be=" + resultValue(be) +
                    "&bl=" + resultValue(bl) +
                    "&bh=" + resultValue(bh);
            log.info("[로하이] 단 : " + canbet + " / bo - " + bo + ", be - " + be + ", bl - " + bl + ", bh - " + bh);
            String gameType = "단폴";
            totalbet = bo + be + bl + bh;
            sendQuery(url, betDate, round, gameType, String.join(", ", betType), totalbet);
        }

    }

    private void sendQuery(String param, String betDate, String round, String gameType, String betType, long price) {
        String json = HttpParsing.getJson(param);
        if (json == null) return;
        PolygonResult result = JsonUtils.toModel(json, PolygonResult.class);
        if (result == null) return;
        PolygonBalance b = new PolygonBalance();
        b.setGame("로하이");
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
