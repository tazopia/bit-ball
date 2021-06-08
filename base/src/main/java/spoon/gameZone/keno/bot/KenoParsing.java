package spoon.gameZone.keno.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneUtils;
import spoon.gameZone.keno.entity.Keno;
import spoon.gameZone.keno.service.KenoBotService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class KenoParsing {

    private final KenoBotService kenoBotService;

    private static boolean isClosing = false;

    private static Date sdate = new Date();

    @Async
    public void parsingGame() {
        isClosing = false;

        int count = 0;
        int round = ZoneConfig.getKeno().getZoneMaker().getRound();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ZoneConfig.getKeno().getZoneMaker().getGameDate());

        for (int i = 0; i < 6; i++) {
            if (cal.getTime().before(sdate)) continue;

            if (kenoBotService.notExist(cal.getTime())) {
                Keno keno = new Keno(round > 288 ? round % 288 : round, cal.getTime());
                if (!ZoneUtils.enabledPower(LocalDateTime.parse(keno.getSdate(), DateTimeFormatter.ofPattern("yyyyMMddHHmm")))) continue;
                keno.setOdds(ZoneConfig.getKeno().getOdds());
                kenoBotService.addGame(keno);
                count++;
            }
            round++;
            cal.add(Calendar.MINUTE, 5);
        }
        sdate = cal.getTime();
        log.info("스피드키노 경기등록 : {}건", count);
    }

    @Async
    public void closingGame() {
        if (isClosing) return;
        isClosing = true;

        Date gameDate = new Date(ZoneConfig.getKeno().getZoneMaker().getGameDate().getTime() - 5 * 60 * 1000);

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getKenoUrl());
        if (json == null) {
            isClosing = false;
            return;
        }

        Keno result = JsonUtils.toModel(json, Keno.class);
        if (result == null) {
            isClosing = false;
            return;
        }

        if (!gameDate.equals(result.getGameDate())) {
            isClosing = false;
            log.error("{} : {}=========================================== 스피드 키노 gameDate is not match", gameDate, result.getGameDate());
            return;
        }

        isClosing = kenoBotService.closingGame(result);

        log.debug("스피드키노 경기 종료 : {}회차", result.getRound());
    }

    @Async
    public void checkResult() {
        kenoBotService.checkResult();
    }

    @Async
    public void deleteGame() {
        kenoBotService.deleteGame(3);
    }
}
