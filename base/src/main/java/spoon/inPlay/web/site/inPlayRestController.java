package spoon.inPlay.web.site;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.Config;
import spoon.inPlay.config.service.InPlayMarketService;
import spoon.inPlay.odds.domain.InPlayDto;
import spoon.inPlay.odds.entity.InPlayOdds;
import spoon.inPlay.odds.service.InPlayGameService;
import spoon.inPlay.odds.service.InPlayOddsService;
import spoon.inPlay.odds.service.InPlayScoreService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("#{config.pathSite}")
public class inPlayRestController {

    private final InPlayGameService inPlayGameService;

    private final InPlayOddsService inPlayOddsService;

    private final InPlayScoreService inPlayScoreService;

    private final InPlayMarketService inPlayMarketService;

    @PostMapping("/inplay/glist")
    public List<InPlayDto.GameData> gameList() {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return inPlayGameService.list();
        }
        return Collections.emptyList();
    }

    @PostMapping("/inplay/olist")
    public Iterable<InPlayOdds> oddsList(@RequestBody InPlayDto.OddsParam param) {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return inPlayOddsService.list(param);
        }
        return Collections.emptyList();
    }

    @PostMapping("/inplay/score")
    public String scoreList(@RequestBody InPlayDto.ScoreParam param) {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return inPlayScoreService.getScore(param.getId());
        }
        return "";
    }

    @PostMapping("/inplay/market")
    public String market() {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return JsonUtils.toString(inPlayMarketService.getList());
        }
        return "";
    }

}
