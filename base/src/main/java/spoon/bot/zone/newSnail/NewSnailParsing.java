package spoon.bot.zone.newSnail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.newSnail.NewSnail;
import spoon.gameZone.newSnail.service.NewSnailBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class NewSnailParsing implements GameBotParsing {

    private NewSnailBotService newSnailBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getNewSnail().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getNewSnail().getZoneMaker().getGameDate());

        for (int i = 0; i < 10; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (newSnailBotService.notExist(cal.getTime())) {
                NewSnail newSnail = new NewSnail(round > 480 ? round % 480 : round, cal.getTime());
                newSnail.setOdds(ZoneConfig.getNewSnail().getOdds());
                newSnailBotService.addGame(newSnail);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 3);
        }
        sdate = cal.getTime();
        log.info("네임드 NEW 달팽이 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getNewSnail().getZoneMaker().getGameDate().getTime() - 3 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getNewSnailUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        NewSnail result = JsonUtils.toModel(json, NewSnail.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = newSnailBotService.closingGame(result);

        log.debug("네임드 NEW 달팽이 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        newSnailBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        newSnailBotService.deleteGame(3);
    }
}
