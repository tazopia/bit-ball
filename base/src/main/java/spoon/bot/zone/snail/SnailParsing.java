package spoon.bot.zone.snail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.snail.Snail;
import spoon.gameZone.snail.service.SnailBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class SnailParsing implements GameBotParsing {

    private SnailBotService snailBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getSnail().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getSnail().getZoneMaker().getGameDate());

        for (int i = 0; i < 6; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (snailBotService.notExist(cal.getTime())) {
                Snail snail = new Snail(round > 288 ? round % 288 : round, cal.getTime());
                snail.setOdds(ZoneConfig.getSnail().getOdds());
                snailBotService.addGame(snail);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 5);
        }
        sdate = cal.getTime();
        log.info("네임드 달팽이 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getSnail().getZoneMaker().getGameDate().getTime() - 5 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSnailUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Snail result = JsonUtils.toModel(json, Snail.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        result.setResult(result.getResult().replaceAll(",", "-"));
        isClosing = snailBotService.closingGame(result);

        log.debug("네임드 달팽이 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        snailBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        snailBotService.deleteGame(3);
    }
}
