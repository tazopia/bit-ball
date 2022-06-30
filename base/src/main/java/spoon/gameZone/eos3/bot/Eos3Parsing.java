package spoon.gameZone.eos3.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos3.entity.Eos3;
import spoon.gameZone.eos3.service.Eos3BotService;

import java.util.Calendar;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos3Parsing {

    private final Eos3BotService eos3BotService;

    private static String sdate = "";

    @Async
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos3Url());
        if (json == null) {
            return;
        }
        Eos3 result = JsonUtils.toModel(json, Eos3.class);
        if (result == null) {
            return;
        }

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getGameDate());
        int round = result.getRound();

        for (int i = 0; i < 6; i++) {
            round++;
            cal.add(Calendar.MINUTE, 3);

            if (eos3BotService.notExist(cal.getTime())) {
                Eos3 eos3 = new Eos3(round, cal.getTime());
                eos3.setOdds(ZoneConfig.getEos3().getOdds());
                eos3BotService.addGame(eos3);
                count++;
            }
        }
        log.info("EOS 3분 파워볼 경기등록 : {}건", count);
    }

    @Async
    public void closingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos3Url());
        if (json == null) {
            return;
        }

        Eos3 result = JsonUtils.toModel(json, Eos3.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = eos3BotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("EOS 3분 파워볼 경기 종료 : {}회차", result.getRound());
    }

    @Async
    public void checkResult() {
        eos3BotService.checkResult();
    }

    @Async
    public void deleteGame() {
        eos3BotService.deleteGame(3);
    }
}
