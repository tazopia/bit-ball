package spoon.gameZone.bitcoin1.bot;

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
import spoon.gameZone.bitcoin1.domain.Bitcoin1Json;
import spoon.gameZone.bitcoin1.entity.Bitcoin1;
import spoon.gameZone.bitcoin1.service.Bitcoin1BotService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin1Parsing {

    private final Bitcoin1BotService bitcoin1BotService;

    private static LocalDateTime gameDate = LocalDateTime.now();

    private static String sdate = "";

    @Async
    public void addGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin1Url());
        if (json == null) return;

        Bitcoin1Json bitcoin1 = JsonUtils.toModel(json, Bitcoin1Json.class);
        if (bitcoin1 == null) return;

        LocalDateTime date = LocalDateTime.parse(bitcoin1.getSdate(), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        int cnt = 0;

        for (int i = 0; i < 30; i++) {
            if (!bitcoin1BotService.exists(date)) {
                Bitcoin1 coin = Bitcoin1.of(date);
                coin.setOdds(ZoneConfig.getBitcoin1().getOdds());
                bitcoin1BotService.add(coin);
                cnt++;
            }
            date = date.plusMinutes(1);
        }
        log.info("비트코인 1분 경기등록 : {}건", cnt);
    }

    @Async
    public void closingGame() {
        LocalDateTime now = LocalDateTime.now();
        if (gameDate.isAfter(now)) return;

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin1Url());
        if (json == null) {
            return;
        }

        Bitcoin1Json result = JsonUtils.toModel(json, Bitcoin1Json.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = bitcoin1BotService.closingGame(result);
        if (success) {
            sdate = result.getSdate();
            gameDate = LocalDateTime.parse(sdate, DateTimeFormatter.ofPattern("yyyyMMddHHmm")).plusMinutes(1);
        }

        log.error("비트코인 1분 종료 : {}회차", result.getRound());
    }

    public void checkResult() {
        bitcoin1BotService.checkResult();
    }

    @Async
    @Transactional
    public void deleteGame(int days) {
        bitcoin1BotService.deleteGame(DateUtils.beforeDays(days));
    }

}
