package spoon.inPlay.bet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.domain.GameConfig;
import spoon.inPlay.bet.domain.InPlayBetDto;
import spoon.inPlay.bet.entity.InPlayBet;
import spoon.inPlay.config.domain.InPlayGameDto;
import spoon.inPlay.odds.domain.LiveScore;
import spoon.inPlay.odds.entity.InPlayGame;
import spoon.inPlay.odds.entity.InPlayOdds;
import spoon.inPlay.odds.service.InPlayGameService;
import spoon.inPlay.odds.service.InPlayOddsService;
import spoon.inPlay.odds.service.InPlayScoreService;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.text.NumberFormat;

@Slf4j
@RequiredArgsConstructor
@Service
public class InPlayBetGameService {

    private final MemberService memberService;

    private final InPlayGameService inPlayGameService;

    private final InPlayScoreService inPlayScoreService;

    private final InPlayOddsService inPlayOddsService;

    private final InPlayBetService inPlayBetService;

    public AjaxResult addGameBet(InPlayBetDto.Bet betDto) {

        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "베팅에 실패하였습니다.");

        Member member = memberService.getMember(userid);
        if (member.isSecession() || !member.isEnabled()) return new AjaxResult(false, "베팅에 실패하였습니다.");

        // 보유금액 체크
        if (member.getMoney() < betDto.getMoney() && member.getRole() == Role.USER) {
            return new AjaxResult(false, "보유머니가 부족합니다.");
        }

        // 최소 베팅 머니
        long min = Config.getGameConfig().getInplayMin()[member.getLevel()];
        if (betDto.getMoney() < min) {
            return new AjaxResult(false, "최소 베팅 금액은 " + NumberFormat.getIntegerInstance().format(min) + "원 입니다.");
        }

        // 최대 베팅 머니
        long max = Config.getGameConfig().getInplayMax()[member.getLevel()];
        if (betDto.getMoney() > max) {
            return new AjaxResult(false, "최대 베팅 금액은 " + NumberFormat.getIntegerInstance().format(max) + "원 입니다.");
        }

        InPlayGame game = inPlayGameService.getOne(betDto.getFixtureId());
        InPlayOdds odds = inPlayOddsService.getOne(betDto.getId(), betDto.getFixtureId());
        LiveScore score = JsonUtils.toModel(inPlayScoreService.getOne(betDto.getFixtureId()).getLiveScore(), LiveScore.class);

        // 현재 스코어가 없다.
        if (score == null || score.getScoreBoard() == null) return new AjaxResult(false, "베팅에 실패하였습니다.");

        // 스코어가 다르다.
        if (!betDto.getScore().equals(currentScore(score))) {
            return new AjaxResult(false, "스코어가 변동되었습니다.");
        }

        // 베팅의 배당이 다르다.
        if ((odds == null || odds.getSettlement() != 0 || betDto.getOdds() != odds.getPrice()) && !betDto.isForce()) {
            return new AjaxResult(false, "현재 베팅이 중지 되었습니다.");
        }

        InPlayBet bet = new InPlayBet();
        bet.addBet(member, betDto, game, odds);

        boolean success = inPlayBetService.addGameBetting(bet);

        if (!success) {
            return new AjaxResult(false, "베팅에 실패하였습니다.");
        }

        return new AjaxResult(true, "베팅을 완료 하였습니다. 정상베팅으로 변경되었는지 필히 확인 바랍니다.");
    }

    private String currentScore(LiveScore score) {
        try {
            assert score != null;
            return score.getScoreBoard().getResults().get(0).getValue() + "-"
                    + score.getScoreBoard().getResults().get(1).getValue();
        } catch (RuntimeException e) {
            log.error("스코어 반환을 하지 못하였습니다.", e);
        }
        return "-";
    }

    public InPlayGameDto.Config getInPlayConfig() {
        String userid = WebUtils.userid();
        InPlayGameDto.Config config = new InPlayGameDto.Config();

        Member member = memberService.getMember(userid);
        GameConfig c = Config.getGameConfig();
        int level = member.getLevel();

        if (member.getRole() == Role.DUMMY) {
            config.setMoney(10000000);
        } else {
            config.setMoney(member.getMoney());
        }

        config.setMax(Config.getGameConfig().getInplayMax()[level]);
        config.setMin(Config.getGameConfig().getInplayMin()[level]);
        config.setWin(Config.getGameConfig().getInplayWin()[level]);

        return config;
    }

    public AjaxResult delete(long id) {
        boolean success = inPlayBetService.deleteUser(id);
        if (success) {
            return new AjaxResult(success);
        }
        return new AjaxResult(false, "베팅삭제에 실패하였습니다.");
    }
}
