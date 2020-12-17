package spoon.web.site.score;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.game.service.GameListService;
import spoon.game.service.sports.SportsService;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("#{config.pathSite}")
public class GameScoreController {

    private GameListService gameListService;

    private SportsService sportsService;

    @RequestMapping(value = "score/{menu:cross|special|live}", method = RequestMethod.GET)
    public String list(ModelMap map, @PathVariable("menu") String menu, String sports,
                       @PageableDefault(size = 40) Pageable pageable) {
        MenuCode menuCode = MenuCode.valueOf(menu.toUpperCase());
        Page<Game> page = gameListService.scoreList(menuCode, sports, pageable);
        List<Game> list = page.getContent();
        String league = "";
        for(Game game : list) {
            if (!league.equals(game.getLeague())) {
                league = game.getLeague();
                game.setSort(0);
            }
        }
        map.addAttribute("page", new PageImpl<>(list, pageable, page.getTotalElements()));
        map.addAttribute("sports", sports);
        map.addAttribute("title", menuCode.getName());
        map.addAttribute("menu", menu);
        map.addAttribute("sportsList", sportsService.getAll());

        return "site/score/list";
    }
}
