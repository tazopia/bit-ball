package spoon.bot.zone.dari;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.dari.Dari;
import spoon.gameZone.dari.service.DariBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class DariParsing implements GameBotParsing {

    private DariBotService dariBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getDari().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getDari().getZoneMaker().getGameDate());

        for (int i = 0; i < 10; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (dariBotService.notExist(cal.getTime())) {
                Dari dari = new Dari(round > 480 ? round % 480 : round, cal.getTime());
                dari.setOdds(ZoneConfig.getDari().getOdds());
                dariBotService.addGame(dari);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 3);
        }
        sdate = cal.getTime();
        log.info("네임드 다리다리 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getDari().getZoneMaker().getGameDate().getTime() - 3 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDariUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Dari result = JsonUtils.toModel(json, Dari.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = dariBotService.closingGame(result);

        log.debug("네임드 다리다리 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        dariBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        dariBotService.deleteGame(3);
    }
}
