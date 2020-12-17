package spoon.bot.zone.luck;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.luck.Luck;
import spoon.gameZone.luck.service.LuckBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LuckParsing implements GameBotParsing {

    private LuckBotService luckBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getLuck().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getLuck().getZoneMaker().getGameDate());

        for (int i = 0; i < 10; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (luckBotService.notExist(cal.getTime())) {
                Luck luck = new Luck(round > 720 ? round % 720 : round, cal.getTime());
                luck.setOdds(ZoneConfig.getLuck().getOdds());
                luckBotService.addGame(luck);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 2);
        }

        log.info("세븐럭 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getLuck().getZoneMaker().getGameDate().getTime() - 2 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLuckUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Luck result = JsonUtils.toModel(json, Luck.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = luckBotService.closingGame(result);

        log.debug("세븐럭 경기 종료 : {}회차", result.getRound());

    }

    @Async
    @Override
    public void checkResult() {
        luckBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        luckBotService.deleteGame(3);
    }
}
