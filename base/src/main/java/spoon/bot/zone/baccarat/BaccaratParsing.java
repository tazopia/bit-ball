package spoon.bot.zone.baccarat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.baccarat.Baccarat;
import spoon.gameZone.baccarat.service.BaccaratBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BaccaratParsing implements GameBotParsing {

    private BaccaratBotService baccaratBotService;

    private static boolean isClosing = false;

    private static Date sdate = DateUtils.beforeMinutes(2);

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getBaccarat().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(ZoneConfig.getOddeven().getZoneMaker().getGameDate().getTime() - 60 * 1000));

        for (int i = 0; i < 30; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (baccaratBotService.notExist(cal.getTime())) {
                Baccarat baccarat = new Baccarat(round > 1440 ? round % 1440 : round, cal.getTime());
                baccarat.setOdds(ZoneConfig.getBaccarat().getOdds());
                baccaratBotService.addGame(baccarat);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 1);
        }
        sdate = cal.getTime();
        log.info("OZ 바카라 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        // 한회차 땡겨 줘야 한다.
        Date gameDate = new Date(ZoneConfig.getBaccarat().getZoneMaker().getGameDate().getTime() - 2 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBaccaratUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Baccarat result = JsonUtils.toModel(json, Baccarat.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        baccaratBotService.closingGame(result);

        log.debug("OZ 바카라 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        baccaratBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        baccaratBotService.deleteGame(3);
    }
}
