package spoon.bot.zone.ladder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ladder.Ladder;
import spoon.gameZone.ladder.service.LadderBotService;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class LadderParsing implements GameBotParsing {

    private LadderBotService ladderBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    @Override
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getLadder().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getLadder().getZoneMaker().getGameDate());

        for (int i = 0; i < 6; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (ladderBotService.notExist(cal.getTime())) {
                Ladder ladder = new Ladder(round > 288 ? round % 288 : round, cal.getTime());
                ladder.setOdds(ZoneConfig.getLadder().getOdds());
                ladderBotService.addGame(ladder);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 5);
        }
        sdate = cal.getTime();
        log.info("네임드 사다리 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getLadder().getZoneMaker().getGameDate().getTime() - 5 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getLadderUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Ladder result = JsonUtils.toModel(json, Ladder.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            return;
        }

        isClosing = ladderBotService.closingGame(result);

        log.debug("네임드 사다리 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        ladderBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        ladderBotService.deleteGame(3);
    }
}
