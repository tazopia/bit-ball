package spoon.gameZone.eos4.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos4.entity.Eos4;
import spoon.gameZone.eos4.service.Eos4BotService;

import java.util.Calendar;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos4Parsing {

    private final Eos4BotService eos4BotService;

    private static String sdate = "";

    @Async
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos4Url());
        if (json == null) {
            return;
        }
        Eos4 result = JsonUtils.toModel(json, Eos4.class);
        if (result == null) {
            return;
        }

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getGameDate());
        int round = result.getRound();

        for (int i = 0; i < 6; i++) {
            round++;
            cal.add(Calendar.MINUTE, 4);

            if (eos4BotService.notExist(cal.getTime())) {
                Eos4 eos4 = new Eos4(round, cal.getTime());
                eos4.setOdds(ZoneConfig.getEos4().getOdds());
                eos4BotService.addGame(eos4);
                count++;
            }
        }
        log.info("EOS 4분 파워볼 경기등록 : {}건", count);
    }

    @Async
    public void closingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos4Url());
        if (json == null) {
            return;
        }

        Eos4 result = JsonUtils.toModel(json, Eos4.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = eos4BotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("EOS 4분 파워볼 경기 종료 : {}회차", result.getRound());
    }

    @Async
    public void checkResult() {
        eos4BotService.checkResult();
    }

    @Async
    public void deleteGame() {
        eos4BotService.deleteGame(3);
    }
}
