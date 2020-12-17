package spoon.bot.zone.kenoLadder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.KenoLadder.KenoLadder;
import spoon.gameZone.KenoLadder.service.KenoLadderBotService;
import spoon.gameZone.ZoneConfig;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class KenoLadderParsing implements GameBotParsing {

    private KenoLadderBotService kenoLadderBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getKenoLadder().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getKenoLadder().getZoneMaker().getGameDate());

        for (int i = 0; i < 6; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (kenoLadderBotService.notExist(cal.getTime())) {
                KenoLadder kenoLadder = new KenoLadder(round > 288 ? round % 288 : round, cal.getTime());
                kenoLadder.setOdds(ZoneConfig.getKenoLadder().getOdds());
                kenoLadderBotService.addGame(kenoLadder);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 5);
        }
        sdate = cal.getTime();
        log.info("키노사다리 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getKenoLadder().getZoneMaker().getGameDate().getTime() - 5 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getKenoLadderUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        KenoLadder result = JsonUtils.toModel(json, KenoLadder.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = kenoLadderBotService.closingGame(result);

        log.debug("키노사다리 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        kenoLadderBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        kenoLadderBotService.deleteGame(3);
    }
}
