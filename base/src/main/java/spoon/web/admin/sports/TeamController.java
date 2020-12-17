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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.game.domain.TeamDto;
import spoon.game.service.sports.SportsService;
import spoon.game.service.sports.TeamService;


@AllArgsConstructor
@Controller("admin.teamController")
@RequestMapping(value = "#{config.pathAdmin}")
public class TeamController {

    private SportsService sportsService;

    private TeamService teamService;

    private static final String REDIRECT_TEAM_PATH = "redirect:" + Config.getPathAdmin() + "/config/team";

    /**
     * 관리자 > 환경설정 > 팀명설정
     */
    @RequestMapping(value = "/config/team", method = RequestMethod.GET)
    public String list(ModelMap map, TeamDto.Command command,
                       @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        map.addAttribute("sports", sportsService.getAll());
        map.addAttribute("page", teamService.getPage(pageable, command));
        map.addAttribute("command", command);

        return "admin/config/team/list";
    }

    /**
     * 관리자 > 환경설정 > 팀명 등록
     */
    @RequestMapping(value = "/config/team", method = RequestMethod.POST)
    public String addTeam(TeamDto.Add add, RedirectAttributes ra) {
        if (isDuplicateTeamName(add)) {
            ra.addFlashAttribute("message", "팀명이 중복되었습니다.");
            return REDIRECT_TEAM_PATH;
        }
        teamService.addTeam(add);
        ra.addFlashAttribute("message", add.getTeamKor() + " 팀을 등록하였습니다.");

        return REDIRECT_TEAM_PATH;
    }

    /**
     * 관리자 > 환경설정 > 팀명 수정
     */
    @RequestMapping(value = "/config/team", method = RequestMethod.PUT)
    public String updateTeam(TeamDto.Update update, RedirectAttributes ra) {
        teamService.updateTeam(update);
        ra.addFlashAttribute("message", update.getTeam() + " 팀을 수정하였습니다.");
        return REDIRECT_TEAM_PATH;
    }

    /**
     * 관리자 > 환경설정 > 팀명 삭제
     */
    @RequestMapping(value = "/config/team", method = RequestMethod.DELETE)
    public String deletePopup(Long id, RedirectAttributes ra) {
        String teamName = teamService.delete(id);
        ra.addFlashAttribute("message", teamName + " 팀을 삭제하였습니다.");
        return REDIRECT_TEAM_PATH;
    }

    @RequestMapping(value = "/config/team/update/{id}", method = RequestMethod.GET)
    public String popupTeam(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("team", teamService.findById(id));
        return "admin/config/team/update";
    }

    @RequestMapping(value = "/config/team/update", method = RequestMethod.POST)
    public String popupTeam(TeamDto.PopupUpdate update, RedirectAttributes ra) {
        teamService.updateTeam(update);
        ra.addFlashAttribute("popup", "closing");
        return "redirect:" + Config.getPathAdmin() + "/config/team/update/" + update.getId();
    }

    private boolean isDuplicateTeamName(TeamDto.Add add) {
        return Config.getTeamMap().containsKey(add.getKey());
    }

}
