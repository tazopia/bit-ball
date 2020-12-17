package spoon.web.admin.sports;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.game.domain.LeagueDto;
import spoon.game.service.sports.LeagueService;
import spoon.game.service.sports.SportsService;
import spoon.support.web.AjaxResult;


@AllArgsConstructor
@Controller("admin.leagueController")
@RequestMapping(value = "#{config.pathAdmin}")
public class LeagueController {

    private LeagueService leagueService;

    private SportsService sportsService;

    private static final String REDIRECT_LEAGUE_PATH = "redirect:" + Config.getPathAdmin() + "/config/league/add";

    /**
     * 관리자 > 환경설정 > 리그설정
     */
    @RequestMapping(value = "/config/league", method = RequestMethod.GET)
    public String list(ModelMap map, LeagueDto.Command command,
                       @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        map.addAttribute("sports", sportsService.getAll());
        map.addAttribute("page", leagueService.getPage(pageable, command));
        map.addAttribute("command", command);

        return "admin/config/league/list";
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 등록 팝업
     */
    @RequestMapping(value = "/config/league/add", method = RequestMethod.GET)
    public String addPopup(ModelMap map) {
        map.addAttribute("sports", sportsService.getAll());
        map.addAttribute("league", new LeagueDto.Add());

        return "admin/config/league/add";
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 등록 팝업 프로세스
     */
    @RequestMapping(value = "/config/league/add", method = RequestMethod.POST)
    public String addPopup(LeagueDto.Add add, MultipartFile file, RedirectAttributes ra) {
        if (isDuplicateLeagueName(add)) {
            ra.addFlashAttribute("message", "리그명이 중복되었습니다.");
            return REDIRECT_LEAGUE_PATH;
        }

        if (file == null || file.getSize() == 0) {
            ra.addFlashAttribute("message", "리그 아이콘은 필수입니다.");
            return REDIRECT_LEAGUE_PATH;
        }

        leagueService.addLeague(add, file);
        ra.addFlashAttribute("popup", "closing");

        return REDIRECT_LEAGUE_PATH;
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 수정 팝업
     */
    @RequestMapping(value = "/config/league/update/{id}", method = RequestMethod.GET)
    public String updatePopup(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("sports", sportsService.getAll());
        map.addAttribute("league", leagueService.findOne(id));
        return "admin/config/league/update";
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 수정 팝업 프로세스
     */
    @RequestMapping(value = "/config/league/update", method = RequestMethod.POST)
    public String updatePopup(LeagueDto.Update update, MultipartFile file, RedirectAttributes ra) {
        leagueService.updateLeague(update, file);
        ra.addFlashAttribute("popup", "closing");
        return REDIRECT_LEAGUE_PATH;
    }

    /**
     * 관리자 > 환경설정 > 리그설정 > 삭제 팝업 프로세스
     */
    @ResponseBody
    @RequestMapping(value = "/config/league/delete", method = RequestMethod.POST)
    public AjaxResult deletePopup(Long id, RedirectAttributes ra) {
        leagueService.delete(id);
        ra.addFlashAttribute("popup", "closing");
        return new AjaxResult(true);
    }

    private boolean isDuplicateLeagueName(LeagueDto.Add add) {
        return Config.getLeagueMap().containsKey(add.getKey());
    }

}
