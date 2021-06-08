package spoon.bot.zone.PowerLadder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneUtils;
import spoon.gameZone.power.Power;
import spoon.gameZone.powerLadder.PowerLadder;
import spoon.gameZone.powerLadder.service.PowerLadderBotService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@Slf4j
@AllArgsConstructor
@Service
public class PowerLadderParsing implements GameBotParsing {

    private PowerLadderBotService powerLadderBotService;

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

            if (powerLadderBotService.notExist(cal.getTime())) {
                PowerLadder powerLadder = new PowerLadder(round, times, cal.getTime());
                if (!ZoneUtils.enabledPower(LocalDateTime.parse(powerLadder.getSdate(), DateTimeFormatter.ofPattern("yyyyMMddHHmm")))) continue;
                powerLadder.setOdds(ZoneConfig.getPowerLadder().getOdds());
                powerLadderBotService.addGame(powerLadder);
                count++;
            }
        }
        log.info("파워사다리 경기등록 : {}건", count);
    }

    @Async
    @Override
    public void closingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getPowerLadderUrl());
        if (json == null) {
            return;
        }

        PowerLadder result = JsonUtils.toModel(json, PowerLadder.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = powerLadderBotService.closingGame(result);
        if (success) sdate = result.getSdate();

        log.debug("파워사다리 경기 종료 : {}회차", result.getRound());
    }

    @Async
    @Override
    public void checkResult() {
        powerLadderBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        powerLadderBotService.deleteGame(3);
    }
}
