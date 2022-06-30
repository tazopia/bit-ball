package spoon.gameZone.eos1.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos1.entity.Eos1;
import spoon.gameZone.eos1.service.Eos1BotService;

import java.util.Calendar;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos1Parsing {

    private final Eos1BotService eos1BotService;

    private static String sdate = "";

    @Async
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos1Url());
        if (json == null) {
            return;
        }
        Eos1 result = JsonUtils.toModel(json, Eos1.class);
        if (result == null) {
            return;
        }

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getGameDate());
        int round = result.getRound();

        for (int i = 0; i < 6; i++) {
            round++;
            cal.add(Calendar.MINUTE, 1);

            if (eos1BotService.notExist(cal.getTime())) {
                Eos1 eos1 = new Eos1(round, cal.getTime());
                eos1.setOdds(ZoneConfig.getEos1().getOdds());
                eos1BotService.addGame(eos1);
                count++;
            }
        }
        log.info("EOS 1분 파워볼 경기등록 : {}건", count);
    }

    @Async
    public void closingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos1Url());
        if (json == null) {
            return;
        }

        Eos1 result = JsonUtils.toModel(json, Eos1.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = eos1BotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("EOS 1분 파워볼 경기 종료 : {}회차", result.getRound());
    }

    @Async
    public void checkResult() {
        eos1BotService.checkResult();
    }

    @Async
    public void deleteGame() {
        eos1BotService.deleteGame(3);
    }
}
