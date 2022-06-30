package spoon.gameZone.eos5.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos5.entity.Eos5;
import spoon.gameZone.eos5.service.Eos5BotService;

import java.util.Calendar;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos5Parsing {

    private final Eos5BotService eos5BotService;

    private static String sdate = "";

    @Async
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos5Url());
        if (json == null) {
            return;
        }
        Eos5 result = JsonUtils.toModel(json, Eos5.class);
        if (result == null) {
            return;
        }

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getGameDate());
        int round = result.getRound();

        for (int i = 0; i < 6; i++) {
            round++;
            cal.add(Calendar.MINUTE, 5);

            if (eos5BotService.notExist(cal.getTime())) {
                Eos5 eos5 = new Eos5(round, cal.getTime());
                eos5.setOdds(ZoneConfig.getEos5().getOdds());
                eos5BotService.addGame(eos5);
                count++;
            }
        }
        log.info("EOS 5분 파워볼 경기등록 : {}건", count);
    }

    @Async
    public void closingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos5Url());
        if (json == null) {
            return;
        }

        Eos5 result = JsonUtils.toModel(json, Eos5.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = eos5BotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("EOS 5분 파워볼 경기 종료 : {}회차", result.getRound());
    }

    @Async
    public void checkResult() {
        eos5BotService.checkResult();
    }

    @Async
    public void deleteGame() {
        eos5BotService.deleteGame(3);
    }
}
