package spoon.bot.zone.dog;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spoon.bot.zone.service.GameBotParsing;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.config.domain.Config;
import spoon.gameZone.dog.Dog;
import spoon.gameZone.dog.DogRepository;
import spoon.gameZone.dog.QDog;
import spoon.gameZone.dog.service.DogBotService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DogParsing implements GameBotParsing {

    private DogRepository dogRepository;

    private DogBotService dogBotService;

    private static QDog q = QDog.dog;

    private static String sdate = "";

    private static String edate = "";

    @Async
    @Override
    public void parsingGame() {
        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDogUrl() + "?sdate=" + sdate);
        if (json == null) {
            return;
        }

        List<Dog> list = JsonUtils.toDogList(json);
        if (list == null) {
            return;
        }

        int saved = 0;
        for (Dog bot : list) {
            if (StringUtils.empty(sdate) || sdate.compareTo(bot.getSdate()) < 0) sdate = bot.getSdate();
            long count = dogRepository.count(q.sdate.eq(bot.getSdate()));

            if (count == 0) {
                dogBotService.addGame(bot);
                saved++;
            }
        }
        log.debug("Bet365 개경주 경기등록 : {}건", saved);
    }

    @Async
    @Override
    public void closingGame() {

        if (StringUtils.notEmpty(edate)) {
            closingLastGame();
        }


        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDogUrl() + "/result?sdate=" + edate);
        if (json == null) {
            return;
        }

        List<Dog> list = JsonUtils.toDogList(json);
        if (list == null) {
            return;
        }

        int closed = 0;
        for (Dog bot : list) {
            if (StringUtils.empty(edate) || edate.compareTo(bot.getSdate()) < 0) edate = bot.getSdate();
            dogBotService.closingGame(bot);
            closed++;
        }

        log.debug("Bet365 개경주 경기종료 : {}건", closed);
    }

    private void closingLastGame() {
        String sdate = dogBotService.getLastGame(edate);
        if (StringUtils.empty(sdate)) return;


        String json = HttpParsing.getJson(Config.getSysConfig().getZone().getDogUrl() + "/result/" + sdate);
        if (json == null) return;

        Dog bot = JsonUtils.toModel(json, Dog.class);
        if (bot == null) return;

        if (bot.isClosing()) {
            dogBotService.closingGame(bot);
        }
    }

    @Async
    @Override
    public void checkResult() {
        dogBotService.checkResult();
    }

    @Async
    @Override
    public void deleteGame() {
        dogBotService.deleteGame(3);
    }
}
