package spoon.bot.zone.soccer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.gameZone.soccer.QSoccer;
import spoon.gameZone.soccer.Soccer;
import spoon.gameZone.soccer.SoccerRepository;
import spoon.gameZone.soccer.service.SoccerBotService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class SoccerParsing implements GameBotParsing {

    private SoccerRepository soccerRepository;

    private SoccerBotService soccerBotService;

    private static QSoccer q = QSoccer.soccer;

    private static String sdate = "";

    private static String edate = "";

    @Async
    @Override
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSoccerUrl() + "?sdate=" + sdate);
        if (json == null) {
            return;
        }

        List<Soccer> list = JsonUtils.toSoccerList(json);
        if (list == null) {
            return;
        }

        int saved = 0;
        for (Soccer bot : list) {
            if (StringUtils.empty(sdate) || sdate.compareTo(bot.getSdate()) < 0) sdate = bot.getSdate();
            long count = soccerRepository.count(q.sdate.eq(bot.getSdate()));

            if (count == 0) {
                soccerBotService.addGame(bot);
                saved++;
            }
        }

        log.debug("Bet365 가상축구 경기등록 : {}건", saved);
    }

    @Async
    @Override
    public void closingGame() {
        if (StringUtils.notEmpty(edate)) {
            closingLastGame();
        }

        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSoccerUrl() + "/result?sdate=" + edate);
        if (json == null) {
            return;
        }

        List<Soccer> list = JsonUtils.toSoccerList(json);
        if (list == null) {
            return;
        }

        int closed = 0;
        for (Soccer bot : list) {
            if (StringUtils.empty(edate) || edate.compareTo(bot.getSdate()) < 0) edate = bot.getSdate();
            soccerBotService.closingGame(bot);
            closed++;
        }

        log.debug("Bet365 가상축구 경기종료 : {}건", closed);
    }

    private void closingLastGame() {
        String sdate = soccerBotService.getLastGame(edate);
        if (StringUtils.empty(sdate)) return;


        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getSoccerUrl() + "/result/" + sdate);
        if (json == null) return;

        Soccer bot = JsonUtils.toModel(json, Soccer.class);
        if (bot == null) return;

        if (bot.isClosing()) {
            soccerBotService.closingGame(bot);
        }
    }

    @Async
    @Override
    public void checkResult() {
        soccerBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        soccerBotService.deleteGame(3);
    }
}
