package spoon.bot.zone.lowhi;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.lowhi.Lowhi;
import spoon.gameZone.lowhi.service.LowhiBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LowhiParsing implements GameBotParsing {

    private LowhiBotService lowhiBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getLowhi().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getLowhi().getZoneMaker().getGameDate());

        for (int i = 0; i < 10; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (lowhiBotService.notExist(cal.getTime())) {
                Lowhi lowhi = new Lowhi(round > 480 ? round % 480 : round, cal.getTime());
                lowhi.setOdds(ZoneConfig.getLowhi().getOdds());
                lowhiBotService.addGame(lowhi);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 3);
        }
        sdate = cal.getTime();
        log.info("하프라인 로하이 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getLowhi().getZoneMaker().getGameDate().getTime() - 3 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLowhiUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Lowhi result = JsonUtils.toModel(json, Lowhi.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = lowhiBotService.closingGame(result);

        log.debug("하프라인 로하이 경기 종료 : {}회차", result.getRound());

    }

    @Async
    @Override
    public void checkResult() {
        lowhiBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        lowhiBotService.deleteGame(3);
    }
}
