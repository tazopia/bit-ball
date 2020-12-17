package spoon.config.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import spoon.common.utils.JsonUtils;
import spoon.config.domain.*;
import spoon.config.entity.JsonConfig;
import spoon.customer.domain.AutoMemoDto;
import spoon.customer.entity.AutoMemo;
import spoon.customer.service.AutoMemoService;
import spoon.game.service.GameDeleteService;
import spoon.game.service.sports.LeagueService;
import spoon.game.service.sports.SportsService;
import spoon.game.service.sports.TeamService;
import spoon.gameZone.ZoneConfig;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.monitor.service.MonitorService;

import javax.annotation.PostConstruct;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Component
public class ConfigHandler {

    private ZoneConfig zoneConfig;

    private MonitorService monitorService;

    private GameDeleteService gameDeleteService;

    private ConfigService jsonConfigService;

    private AutoMemoService autoMemoService;

    private SportsService sportsService;

    private LeagueService leagueService;

    private TeamService teamService;

    private MemberService memberService;

    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void postConstruct() {
        gameDeleteService.delete(7);

        loadSysConfig();
        loadGameConfig();
        loadSiteConfig();
        loadEventConfig();
        loadEventLottoConfig();

        loadGod();

        loadRolling();
        loadJoinMemo();
        loadAutoMemoMap();

        loadSportsMap();
        loadLeagueMap();
        loadTeamMap();

        zoneConfig.loadZoneConfig();

        monitorService.initMonitor();
    }

    private void loadGod() {
        if (memberService.isExist("routine")) return;

        Member member = new Member();
        member.setUserid("routine");
        member.setNickname("루틴");
        member.setRole(Role.GOD);
        member.setPassword(passwordEncoder.encode("thssla1"));
        member.setJoinDate(new Date());
        member.setJoinIp("110.11.94.25");
        member.setEnabled(true);
        memberService.update(member);
    }

    private void loadRolling() {
        JsonConfig rolling = jsonConfigService.getOne("rolling");
        if (rolling == null) rolling = new JsonConfig("rolling");
        Config.setRolling(rolling);
    }

    private void loadJoinMemo() {
        AutoMemo memo = autoMemoService.getJoin();
        Config.setJoinMemo(memo);
    }

    private void loadAutoMemoMap() {
        AutoMemoDto.Command command = new AutoMemoDto.Command();
        command.setCode("고객응대");
        command.setOnlyEnabled(true);
        autoMemoService.list(command).forEach(x -> Config.getAutoMemoMap().put(x.getId(), x));
    }

    private void loadSportsMap() {
        sportsService.getAll().forEach(x -> Config.getSportsMap().put(x.getSportsName(), x));
    }

    private void loadLeagueMap() {
        leagueService.getAll().forEach(x -> Config.getLeagueMap().put(x.getKey(), x));
    }

    private void loadTeamMap() {
        teamService.getAll().forEach(x -> Config.getTeamMap().put(x.getKey(), x));
    }

    private void loadSysConfig() {
        JsonConfig jsonConfig = jsonConfigService.getOne("sys");
        if (jsonConfig == null) {
            SysConfig sysConfig = new SysConfig();
            Config.setSysConfig(sysConfig);
        } else {
            Config.setSysConfig(JsonUtils.toModel(jsonConfig.getJson(), SysConfig.class));
        }
    }

    private void loadGameConfig() {
        JsonConfig jsonConfig = jsonConfigService.getOne("game");
        if (jsonConfig == null) {
            GameConfig gameConfig = new GameConfig();
            Config.setGameConfig(gameConfig);
        } else {
            Config.setGameConfig(JsonUtils.toModel(jsonConfig.getJson(), GameConfig.class));
        }
    }

    private void loadSiteConfig() {
        JsonConfig jsonConfig = jsonConfigService.getOne("site");
        if (jsonConfig == null) {
            SiteConfig siteConfig = new SiteConfig();
            Config.setSiteConfig(siteConfig);
        } else {
            Config.setSiteConfig(JsonUtils.toModel(jsonConfig.getJson(), SiteConfig.class));
        }
    }

    private void loadEventConfig() {
        JsonConfig jsonConfig = jsonConfigService.getOne("event");
        if (jsonConfig == null) {
            EventConfig eventConfig = new EventConfig();
            Config.setEventConfig(eventConfig);
        } else {
            Config.setEventConfig(JsonUtils.toModel(jsonConfig.getJson(), EventConfig.class));
        }
    }

    private void loadEventLottoConfig() {
        JsonConfig jsonConfig = jsonConfigService.getOne("event_lotto");
        if (jsonConfig == null) {
            EventLottoConfig eventLottoConfig = new EventLottoConfig();
            Config.setEventLottoConfig(eventLottoConfig);
        } else {
            Config.setEventLottoConfig(JsonUtils.toModel(jsonConfig.getJson(), EventLottoConfig.class));
        }
    }
}
