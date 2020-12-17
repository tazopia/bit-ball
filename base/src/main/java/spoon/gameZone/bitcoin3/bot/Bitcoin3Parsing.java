package spoon.gameZone.bitcoin3.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.bitcoin3.domain.Bitcoin3Json;
import spoon.gameZone.bitcoin3.entity.Bitcoin3;
import spoon.gameZone.bitcoin3.service.Bitcoin3BotService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin3Parsing {

    private final Bitcoin3BotService bitcoin3BotService;

    private static LocalDateTime gameDate = LocalDateTime.now();

    private static String sdate = "";

    @Async
    public void addGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin3Url());
        if (json == null) return;

        Bitcoin3Json bitcoin3 = JsonUtils.toModel(json, Bitcoin3Json.class);
        if (bitcoin3 == null) return;

        LocalDateTime date = LocalDateTime.parse(bitcoin3.getSdate(), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        int cnt = 0;

        for (int i = 0; i < 10; i++) {
            if (!bitcoin3BotService.exists(date)) {
                Bitcoin3 coin = Bitcoin3.of(date);
                coin.setOdds(ZoneConfig.getBitcoin3().getOdds());
                bitcoin3BotService.add(coin);
                cnt++;
            }
            date = date.plusMinutes(3);
        }
        log.info("비트코인 3분 경기등록 : {}건", cnt);
    }

    @Async
    public void closingGame() {
        LocalDateTime now = LocalDateTime.now();
        if (gameDate.isAfter(now)) return;

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin3Url());
        if (json == null) {
            return;
        }

        Bitcoin3Json result = JsonUtils.toModel(json, Bitcoin3Json.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = bitcoin3BotService.closingGame(result);
        if (success) {
            sdate = result.getSdate();
            gameDate = LocalDateTime.parse(sdate, DateTimeFormatter.ofPattern("yyyyMMddHHmm")).plusMinutes(3);
        }

        log.error("비트코인 3분 종료 : {}회차", result.getRound());
    }

    public void checkResult() {
        bitcoin3BotService.checkResult();
    }

    @Async
    @Transactional
    public void deleteGame(int days) {
        bitcoin3BotService.deleteGame(DateUtils.beforeDays(days));
    }

}
