package spoon.bot.zone.power;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.power.Power;
import spoon.gameZone.power.service.PowerBotService;

import java.util.Calendar;

@Slf4j
@AllArgsConstructor
@Service
public class PowerParsing implements GameBotParsing {

    private PowerBotService powerBotService;

    private static String sdate = "";

    @Async
    @Override
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerUrl());
        if (json == null) {
            return;
        }
        Power result = JsonUtils.toModel(json, Power.class);
        if (result == null) {
            return;
        }

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getGameDate());
        int times = result.getTimes();
        int round = result.getRound();

        for (int i = 0; i < 6; i++) {
            times++;
            round++;
            cal.add(Calendar.MINUTE, 5);

            if (powerBotService.notExist(cal.getTime())) {
                Power power = new Power(round, times, cal.getTime());
                power.setOdds(ZoneConfig.getPower().getOdds());
                powerBotService.addGame(power);
                count++;
            }
        }
        log.info("파워볼 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
//        int times = ZoneConfig.getPower().getPowerMaker().getTimes();
//        Date gameDate = ZoneConfig.getPower().getPowerMaker().getGameDate(times);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerUrl());
        if (json == null) {
            return;
        }

        Power result = JsonUtils.toModel(json, Power.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = powerBotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("파워볼 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        powerBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        powerBotService.deleteGame(3);
    }
}
