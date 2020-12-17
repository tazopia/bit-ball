package spoon.inPlay.web.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.config.entity.InPlayLeague;
import spoon.inPlay.config.entity.InPlaySports;
import spoon.inPlay.config.entity.InPlayTeam;
import spoon.inPlay.odds.domain.InPlayDto;
import spoon.inPlay.odds.entity.InPlayGame;
import spoon.inPlay.odds.entity.InPlayOdds;
import spoon.inPlay.odds.service.InPlayGameService;
import spoon.inPlay.odds.service.InPlayOddsService;
import spoon.inPlay.odds.service.InPlayScoreService;
import spoon.inPlay.support.extend.OptionalMap;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController(value = "admin.inPlayRestController")
@RequestMapping("#{config.pathAdmin}")
public class inPlayRestController {

    private final InPlayGameService inPlayGameService;

    private final InPlayOddsService inPlayOddsService;

    private final InPlayScoreService inPlayScoreService;

    @PostMapping("/inplay/glist")
    public List<InPlayDto.GameData> gameList() {
        return inPlayGameService.list();
    }

    @PostMapping("/inplay/olist")
    public Iterable<InPlayOdds> oddsList(@RequestBody InPlayDto.OddsParam param) {
        return inPlayOddsService.list(param);
    }

    @PostMapping("/inplay/score")
    public String scoreList(@RequestBody InPlayDto.ScoreParam param) {
        return inPlayScoreService.getScore(param.getId());
    }

    @PostMapping("/inplay/sports")
    public OptionalMap<InPlaySports> sports() {
        return InPlayConfig.getSports();
    }

    @PostMapping("/inplay/league")
    public OptionalMap<InPlayLeague> league() {
        return InPlayConfig.getLeague();
    }

    @PostMapping("/inplay/team")
    public OptionalMap<InPlayTeam> team() {
        return InPlayConfig.getTeam();
    }
}
