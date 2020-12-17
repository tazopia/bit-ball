package spoon.web.admin.sports;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spoon.config.domain.Config;
import spoon.game.domain.SportsDto;
import spoon.game.service.sports.SportsService;
import spoon.support.web.AjaxResult;


@Slf4j
@AllArgsConstructor
@Controller("admin.sportsController")
@RequestMapping(value = "#{config.pathAdmin}")
public class SportsController {

    private SportsService sportsService;

    private static final String REDIRECT_SPORTS_PATH = "redirect:" + Config.getPathAdmin() + "/config/sports/add";

    /**
     * 관리자 > 환경설정 > 종목설정
     */
    @RequestMapping(value = "/config/sports", method = RequestMethod.GET)
    public String list(ModelMap map) {
        map.addAttribute("list", sportsService.getAll());

        return "admin/config/sports/list";
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 등록 팝업
     */
    @RequestMapping(value = "/config/sports/add", method = RequestMethod.GET)
    public String addPopup(ModelMap map) {
        map.addAttribute("sports", new SportsDto.Add());
        return "admin/config/sports/add";
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 등록 팝업 프로세스
     */
    @RequestMapping(value = "/config/sports/add", method = RequestMethod.POST)
    public String addPopup(SportsDto.Add add, MultipartFile file, RedirectAttributes ra) {
        if (isDuplicateSports(add)) {
            ra.addFlashAttribute("message", "종목명이 중복되었습니다.");
            return REDIRECT_SPORTS_PATH;
        }

        if (file == null || file.getSize() == 0) {
            ra.addFlashAttribute("message", "스포츠 아이콘은 필수입니다.");
            return REDIRECT_SPORTS_PATH;
        }

        sportsService.addSports(add, file);
        ra.addFlashAttribute("popup", "closing");

        return REDIRECT_SPORTS_PATH;
    }


    /**
     * 관리자 > 환경설정 > 종목설정 > 수정 팝업
     */
    @RequestMapping(value = "/config/sports/update/{id}", method = RequestMethod.GET)
    public String updatePopup(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("sports", sportsService.findOne(id));
        return "admin/config/sports/update";
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 수정 팝업 프로세스
     */
    @RequestMapping(value = "/config/sports/update", method = RequestMethod.POST)
    public String updatePopup(SportsDto.Update update, MultipartFile file, RedirectAttributes ra) {
        sportsService.updateSports(update, file);
        ra.addFlashAttribute("popup", "closing");
        return REDIRECT_SPORTS_PATH;
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 삭제 팝업 프로세스
     */
    @ResponseBody
    @RequestMapping(value = "/config/sports/delete", method = RequestMethod.POST)
    public AjaxResult delete(Long id) {
        sportsService.delete(id);
        return new AjaxResult(true);
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 정렬 업
     */
    @ResponseBody
    @RequestMapping(value = "/config/sports/up", method = RequestMethod.POST)
    public AjaxResult sortUp(Long id) {
        sportsService.up(id);
        return new AjaxResult(true);
    }

    /**
     * 관리자 > 환경설정 > 종목설정 > 정렬 다운
     */
    @ResponseBody
    @RequestMapping(value = "/config/sports/down", method = RequestMethod.POST)
    public AjaxResult sortDown(Long id) {
        sportsService.down(id);
        return new AjaxResult(true);
    }

    private boolean isDuplicateSports(SportsDto.Add add) {
        return Config.getSportsMap().containsKey(add.getSportsName());
    }
}
