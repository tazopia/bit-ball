package spoon.config.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.domain.ConfigDto;
import spoon.config.domain.GameConfig;
import spoon.game.domain.MenuCode;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;

@AllArgsConstructor
@Service
public class GameConfigServiceImpl implements GameConfigService {

    private MemberService memberService;

    @Override
    public ConfigDto.Game gameConfig(MenuCode menuCode) {
        String userid = WebUtils.userid();
        ConfigDto.Game config = new ConfigDto.Game();
        if (userid == null) return config;

        Member member = memberService.getMember(userid);
        GameConfig c = Config.getGameConfig();
        int level = member.getLevel();

        if (member.getRole() == Role.DUMMY) {
            config.setMoney(10000000);
        } else {
            config.setMoney(member.getMoney());
        }

        switch (menuCode) {
            case CROSS:
            case MATCH:
            case HANDICAP:
                config.setMax(c.getCrossMax()[level]);
                config.setWin(c.getCrossWin()[level]);
                config.setMin(c.getCrossMin()[level]);
                config.setMark(c.getCrossMark()[level]);
                config.setOne(c.isCrossOne());
                config.setOneMax(c.getCrossOneMax()[level]);
                config.setOneWin(c.getCrossOneWin()[level]);
                config.setOneMin(c.getCrossOneMin()[level]);
                if (c.getBonus()[0]) {
                    config.setBonusOdds(c.getBonusOdds());
                }
                if (c.isEnabledMinOdds()) config.setMinOdd(c.getMinOdds());
                break;
            case SPECIAL:
                config.setMax(c.getSpecialMax()[level]);
                config.setWin(c.getSpecialWin()[level]);
                config.setMin(c.getSpecialMin()[level]);
                config.setMark(c.getSpecialMark()[level]);
                config.setOne(c.isSpecialOne());
                config.setOneMax(c.getSpecialOneMax()[level]);
                config.setOneWin(c.getSpecialOneWin()[level]);
                config.setOneMin(c.getSpecialOneMin()[level]);
                if (c.getBonus()[1]) {
                    config.setBonusOdds(c.getBonusOdds());
                }
                if (c.isEnabledMinOdds()) config.setMinOdd(c.getMinOdds());
                break;
            case LIVE:
                config.setMax(c.getLiveMax()[level]);
                config.setWin(c.getLiveWin()[level]);
                config.setMin(c.getLiveMin()[level]);
                config.setMark(c.getLiveMark()[level]);
                config.setOne(c.isLiveOne());
                config.setOneMax(c.getLiveOneMax()[level]);
                config.setOneWin(c.getLiveOneWin()[level]);
                config.setOneMin(c.getLiveOneMin()[level]);
                if (c.getBonus()[2]) {
                    config.setBonusOdds(c.getBonusOdds());
                }
                if (c.isEnabledMinOdds()) config.setMinOdd(c.getMinOdds());
                break;
        }

        return config;
    }

}
