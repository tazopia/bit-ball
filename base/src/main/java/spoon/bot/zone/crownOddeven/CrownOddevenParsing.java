package spoon.bot.zone.crownOddeven;

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
import spoon.gameZone.crownOddeven.CrownOddeven;
import spoon.gameZone.crownOddeven.service.CrownOddevenBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownOddevenParsing implements GameBotParsing {

    private CrownOddevenBotService crownOddevenBotService;

    private static boolean isClosing = false;

    private static Date sdate = DateUtils.beforeMinutes(2);

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getCrownOddeven().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(ZoneConfig.getCrownOddeven().getZoneMaker().getGameDate().getTime() - 60 * 1000));

        for (int i = 0; i < 30; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (crownOddevenBotService.notExist(cal.getTime())) {
                CrownOddeven crownOddeven = new CrownOddeven(round > 1440 ? round % 1440 : round, cal.getTime());
                crownOddeven.setOdds(ZoneConfig.getCrownOddeven().getOdds());
                crownOddevenBotService.addGame(crownOddeven);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 1);
        }
        sdate = cal.getTime();
        log.info("CROWN 홀짝 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        // 한회차 땡겨 줘야 한다.
        Date gameDate = new Date(ZoneConfig.getCrownOddeven().getZoneMaker().getGameDate().getTime() - 2 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownOddevenUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        CrownOddeven result = JsonUtils.toModel(json, CrownOddeven.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        crownOddevenBotService.closingGame(result);

        log.debug("CROWN 홀짝 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        crownOddevenBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        crownOddevenBotService.deleteGame(3);
    }
}
