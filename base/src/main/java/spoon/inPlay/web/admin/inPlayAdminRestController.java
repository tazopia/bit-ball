package spoon.inPlay.web.admin;


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
@RestController("admin.inPlayAdminRestController")
@RequestMapping("#{config.pathAdmin}")
public class inPlayAdminRestController {

    private final InPlayGameService inPlayGameService;

    private final InPlayOddsService inPlayOddsService;

    private final InPlayScoreService inPlayScoreService;

    private final InPlayMarketService inPlayMarketService;

    @PostMapping("inplay/game/glist")
    public List<InPlayDto.GameData> gameList() {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return inPlayGameService.list();
        }
        return Collections.emptyList();
    }

    @PostMapping("inplay/game/olist")
    public Iterable<InPlayOdds> oddsList(@RequestBody InPlayDto.OddsParam param) {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return inPlayOddsService.adminList(param);
        }
        return Collections.emptyList();
    }

    @PostMapping("inplay/game/score")
    public String scoreList(@RequestBody InPlayDto.ScoreParam param) {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return inPlayScoreService.getScore(param.getId());
        }
        return "";
    }

    @PostMapping("inplay/game/market")
    public String market() {
        if (Config.getSysConfig().getSports().isCanInplay()) {
            return JsonUtils.toString(inPlayMarketService.getAdminList());
        }
        return "";
    }

}
