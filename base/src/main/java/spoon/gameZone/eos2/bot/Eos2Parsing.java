package spoon.gameZone.eos2.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.eos2.entity.Eos2;
import spoon.gameZone.eos2.service.Eos2BotService;

import java.util.Calendar;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eos2Parsing {

    private final Eos2BotService eos2BotService;

    private static String sdate = "";

    @Async
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos2Url());
        if (json == null) {
            return;
        }
        Eos2 result = JsonUtils.toModel(json, Eos2.class);
        if (result == null) {
            return;
        }

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getGameDate());
        int round = result.getRound();

        for (int i = 0; i < 6; i++) {
            round++;
            cal.add(Calendar.MINUTE, 2);

            if (eos2BotService.notExist(cal.getTime())) {
                Eos2 eos2 = new Eos2(round, cal.getTime());
                eos2.setOdds(ZoneConfig.getEos2().getOdds());
                eos2BotService.addGame(eos2);
                count++;
            }
        }
        log.info("EOS 2분 파워볼 경기등록 : {}건", count);
    }

    @Async
    public void closingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getEos2Url());
        if (json == null) {
            return;
        }

        Eos2 result = JsonUtils.toModel(json, Eos2.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = eos2BotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("EOS 2분 파워볼 경기 종료 : {}회차", result.getRound());
    }

    @Async
    public void checkResult() {
        eos2BotService.checkResult();
    }

    @Async
    public void deleteGame() {
        eos2BotService.deleteGame(3);
    }
}
