package spoon.gameZone.bitcoin5.bot;

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
import spoon.gameZone.bitcoin5.domain.Bitcoin5Json;
import spoon.gameZone.bitcoin5.entity.Bitcoin5;
import spoon.gameZone.bitcoin5.service.Bitcoin5BotService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class Bitcoin5Parsing {

    private final Bitcoin5BotService bitcoin5BotService;

    private static LocalDateTime gameDate = LocalDateTime.now();

    private static String sdate = "";

    @Async
    public void addGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin5Url());
        if (json == null) return;

        Bitcoin5Json bitcoin5 = JsonUtils.toModel(json, Bitcoin5Json.class);
        if (bitcoin5 == null) return;

        LocalDateTime date = LocalDateTime.parse(bitcoin5.getSdate(), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        int cnt = 0;

        for (int i = 0; i < 6; i++) {
            if (!bitcoin5BotService.exists(date)) {
                Bitcoin5 coin = Bitcoin5.of(date);
                coin.setOdds(ZoneConfig.getBitcoin5().getOdds());
                bitcoin5BotService.add(coin);
                cnt++;
            }
            date = date.plusMinutes(5);
        }
        log.info("비트코인 5분 경기등록 : {}건", cnt);
    }

    @Async
    public void closingGame() {
        LocalDateTime now = LocalDateTime.now();
        if (gameDate.isAfter(now)) return;

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getBitcoin5Url());
        if (json == null) {
            return;
        }

        Bitcoin5Json result = JsonUtils.toModel(json, Bitcoin5Json.class);
        if (result == null) {
            return;
        }

        if (sdate.equals(result.getSdate())) {
            return;
        }

        boolean success = bitcoin5BotService.closingGame(result);
        if (success) {
            sdate = result.getSdate();
            gameDate = LocalDateTime.parse(sdate, DateTimeFormatter.ofPattern("yyyyMMddHHmm")).plusMinutes(5);
        }

        log.error("비트코인 5분 종료 : {}회차", result.getRound());
    }

    public void checkResult() {
        bitcoin5BotService.checkResult();
    }

    @Async
    @Transactional
    public void deleteGame(int days) {
        bitcoin5BotService.deleteGame(DateUtils.beforeDays(days));
    }

}
