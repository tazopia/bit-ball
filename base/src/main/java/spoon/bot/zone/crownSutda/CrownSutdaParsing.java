package spoon.bot.zone.crownSutda;

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
import spoon.gameZone.crownSutda.CrownSutda;
import spoon.gameZone.crownSutda.service.CrownSutdaBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class CrownSutdaParsing implements GameBotParsing {

    private CrownSutdaBotService crownSutdaBotService;

    private static boolean isClosing = false;

    private static Date sdate = DateUtils.beforeMinutes(2);

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getCrownSutda().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(ZoneConfig.getCrownBaccarat().getZoneMaker().getGameDate().getTime() - 60 * 1000));

        for (int i = 0; i < 30; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (crownSutdaBotService.notExist(cal.getTime())) {
                CrownSutda crownSutda = new CrownSutda(round > 1440 ? round % 1440 : round, cal.getTime());
                crownSutda.setOdds(ZoneConfig.getCrownSutda().getOdds());
                crownSutdaBotService.addGame(crownSutda);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 1);
        }
        sdate = cal.getTime();
        log.info("CROWN 섰다 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        // 한회차 땡겨 줘야 한다.
        Date gameDate = new Date(ZoneConfig.getCrownSutda().getZoneMaker().getGameDate().getTime() - 2 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getCrownSutdaUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        CrownSutda result = JsonUtils.toModel(json, CrownSutda.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        crownSutdaBotService.closingGame(result);

        log.debug("CROWN 섰다 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        crownSutdaBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        crownSutdaBotService.deleteGame(3);
    }
}
