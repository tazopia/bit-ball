package spoon.bot.zone.aladdin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.aladdin.Aladdin;
import spoon.gameZone.aladdin.service.AladdinBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class AladdinParsing implements GameBotParsing {

    private AladdinBotService aladdinBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getAladdin().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getAladdin().getZoneMaker().getGameDate());

        for (int i = 0; i < 10; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (aladdinBotService.notExist(cal.getTime())) {
                Aladdin aladdin = new Aladdin(round > 480 ? round % 480 : round, cal.getTime());
                aladdin.setOdds(ZoneConfig.getAladdin().getOdds());
                aladdinBotService.addGame(aladdin);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 3);
        }

        log.info("하프라인 알라딘 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getAladdin().getZoneMaker().getGameDate().getTime() - 3 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getAladdinUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Aladdin result = JsonUtils.toModel(json, Aladdin.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = aladdinBotService.closingGame(result);

        log.debug("하프라인 알라딘 경기 종료 : {}회차", result.getRound());

    }

    @Async
    @Override
    public void checkResult() {
        aladdinBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        aladdinBotService.deleteGame(3);
    }
}
