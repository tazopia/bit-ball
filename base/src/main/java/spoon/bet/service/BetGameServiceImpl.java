package spoon.bet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.bet.domain.BetDto;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.bet.entity.QBetItem;
import spoon.bet.repository.BetItemRepository;
import spoon.common.utils.DateUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.domain.ConfigDto;
import spoon.config.service.GameConfigService;
import spoon.game.domain.GameCode;
import spoon.game.domain.MenuCode;
import spoon.game.entity.Game;
import spoon.game.repository.GameRepository;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static spoon.game.domain.MenuCode.isSports;

@Slf4j
@AllArgsConstructor
@Service
public class BetGameServiceImpl implements BetGameService {

    private BetService betService;

    private MemberService memberService;

    private GameConfigService gameConfigService;

    private GameRepository gameRepository;

    private BetItemRepository betItemRepository;

    @Override
    public AjaxResult addGameBetting(BetDto.BetGame betGame) {
        AjaxResult result = new AjaxResult();
        String userid = WebUtils.userid();

        if (userid == null) {
            result.setMessage("베팅에 실패하였습니다.");
            return result;
        }

        Member member = memberService.getMember(userid);
        if (member.isSecession() || !member.isEnabled()) {
            result.setMessage("베팅에 실패하였습니다.");
            return result;
        }

        int gameCnt = betGame.getIds().length;
        ConfigDto.Game gameConfig = gameConfigService.gameConfig(betGame.getMenuCode());

        // 보유금액 체크
        if (member.getMoney() < betGame.getBetMoney() && member.getRole() == Role.USER) {
            result.setMessage("보유머니가 부족합니다.");
            return result;
        }

        // 단폴더 베팅 가능 체크
        if (gameCnt == 1 && !gameConfig.isOne()) {
            result.setMessage("단폴더 베팅은 불가능 합니다.");
            return result;
        }

        // 최대 폴더 체크
        if (gameCnt > gameConfig.getSportsMaxFolder()) {
            result.setMessage("최대 조합 가능 폴더는 " + gameConfig.getSportsMaxFolder() + " 입니다.");
            return result;
        }

        // 최소 베팅 머니
        int minMoney = gameCnt == 1 ? gameConfig.getOneMin() : gameConfig.getMin();
        if (betGame.getBetMoney() < minMoney) {
            result.setMessage("최소 베팅 금액은 " + NumberFormat.getIntegerInstance().format(minMoney) + "원 입니다.");
            return result;
        }

        // 최대 베팅 머니
        int maxMoney = gameCnt == 1 ? gameConfig.getOneMax() : gameConfig.getMax();
        if (betGame.getBetMoney() > maxMoney) {
            result.setMessage("최대 베팅 금액은 " + NumberFormat.getIntegerInstance().format(maxMoney) + "원 입니다.");
            return result;
        }

        List<BetItem> betItems = new ArrayList<>();
        BigDecimal betOdds = BigDecimal.valueOf(1);
        Date startDate = DateUtils.beforeDays(-1);
        Date endDate = new Date();

        for (int i = 0; i < betGame.getIds().length; i++) {
            String pos = betGame.getBets()[i];
            Double odds = betGame.getOdds()[i];
            Game game = gameRepository.findOne(betGame.getIds()[i]);

            if (startDate.after(game.getGameDate())) {
                startDate = game.getGameDate();
            }

            if (endDate.before(game.getGameDate())) {
                endDate = game.getGameDate();
            }

            // 마감경기 체크
            if (!game.isBeforeGameDate()) {
                result.setMessage("마감된 경기가 있습니다. 다시 베팅하여 주세요.");
                return result;
            }

            // 베팅 중지 체크
            switch (pos) {
                case "home":
                    if (!game.isBetHome()) {
                        result.setMessage("베팅 중지된 경기가 있습니다. 다시 베팅하여 주세요.");
                        return result;
                    }
                    if (game.getOddsHome() != odds && !betGame.isForce()) {
                        result.setMessage("배당이 변경된 경기가 있습니다. 다시 베팅하여 주세요.");
                        return result;
                    }
                    betOdds = betOdds.multiply(BigDecimal.valueOf(game.getOddsHome()));
                    break;
                case "draw":
                    if (!game.isBetDraw()) {
                        result.setMessage("베팅 중지된 경기가 있습니다. 다시 베팅하여 주세요.");
                        return result;
                    }
                    if (game.getOddsDraw() != odds && !betGame.isForce()) {
                        result.setMessage("배당이 변경된 경기가 있습니다. 다시 베팅하여 주세요.");
                        return result;
                    }
                    betOdds = betOdds.multiply(BigDecimal.valueOf(game.getOddsDraw()));
                    break;
                case "away":
                    if (!game.isBetAway()) {
                        result.setMessage("베팅 중지된 경기가 있습니다. 다시 베팅하여 주세요.");
                        return result;
                    }
                    if (game.getOddsAway() != odds && !betGame.isForce()) {
                        result.setMessage("배당이 변경된 경기가 있습니다. 다시 베팅하여 주세요.");
                        return result;
                    }
                    betOdds = betOdds.multiply(BigDecimal.valueOf(game.getOddsAway()));
                    break;
            }
            BetItem betItem = new BetItem(game, userid, member.getRole(), pos, betGame.getBetMoney());
            betItems.add(betItem);
        }

        // 최대 적중 금액
        int winMoney = gameCnt == 1 ? gameConfig.getOneWin() : gameConfig.getWin();
        if (betOdds.setScale(2, BigDecimal.ROUND_DOWN).doubleValue() * betGame.getBetMoney() > winMoney) {
            result.setMessage("최대 적중 금액은 " + NumberFormat.getIntegerInstance().format(winMoney) + "원 입니다.");
            return result;
        }

        Bet bet = new Bet(member.getUser());
        bet.setBetMoney(betGame.getBetMoney());
        bet.setBetItems(betItems);
        bet.setMenuCode(betGame.getMenuCode());
        bet.setBetCount(gameCnt);
        bet.setBetDate(new Date());
        bet.setStartDate(startDate);
        bet.setEndDate(endDate);
        bet.setIp(WebUtils.ip());

        // 이벤트 배당하락 또는 배당상승 체크 (betOdds, expMoney 채워 줌)
        bet.betExpOdds();
        if (gameCnt == 1 && Config.getGameConfig().getBonusOne() > 0 && betGame.getMenuCode() != MenuCode.LIVE) {
            result.setValue("one");
        }

        // 중복베팅 축베팅 확인
        if (isSports(betGame.getMenuCode())) {
            // 중복베팅 확인
            for (Bet oldBet : betService.getCurrentBet(userid)) {
                int count = 0;
                for (BetItem oldItem : oldBet.getBetItems()) {
                    for (BetItem item : bet.getBetItems()) {
                        // 게임코드, 메뉴코드, 그룹아이디로 체크, 홈팀명으로 구분
                        if (oldItem.getGroupId().equals(item.getGroupId())
                                && ("본사".equals(item.getSiteCode()) || oldItem.getTeamHome().equals(item.getTeamHome()))// 추가
                                && oldItem.getGameCode() == item.getGameCode()
                                && oldItem.getMenuCode() == item.getMenuCode()
                                && !oldItem.isCancel()) {
                            if (item.getGameCode() == GameCode.HANDICAP && item.getHandicap() != oldItem.getHandicap() && !Config.getSysConfig().getSports().isEnableSpread()) {
                                result.setMessage(oldItem.getTeamHome() + " VS" + oldItem.getTeamAway() + " 경기의 스프레드 핸디는 한번만 선택 가능 합니다.");
                                return result;
                            } else if (item.getGameCode() == GameCode.OVER_UNDER && item.getHandicap() != oldItem.getHandicap() && !Config.getSysConfig().getSports().isEnableSpread()) {
                                result.setMessage(oldItem.getTeamHome() + " VS" + oldItem.getTeamAway() + " 경기의 스프레드 오버언더는 한번만 선택 가능 합니다.");
                                return result;
                            }

                            if (Config.getSysConfig().getSports().isEnableSure()) {
                                // 양방 허용일때 동일 결과베팅만 카운팅
                                if (oldItem.getBetTeam().equals(item.getBetTeam())) {
                                    count++;
                                }
                            } else {
                                // 양방 허용이 아닐때 동일 조합 카운팅
                                count++;
                            }
                        }
                    }
                    if (bet.getBetCount() == count && oldBet.getBetCount() == count) {
                        result.setMessage("중복된 베팅이 있습니다.\n\n동일 조합베팅은 허용되지 않습니다.");
                        return result;
                    }
                }
            }

            // 축베팅 확인
            for (BetItem item : bet.getBetItems()) {
                long betSumMoney = 0;
                long betExpMoney = 0;
                int betCnt = 1;

                for (BetItem oldItem : betService.betMoneyByItem(userid, item.getGameId(), item.getBetTeam())) {
                    if (oldItem.getBet().isClosing() || oldItem.getBet().isCancel()) continue;
                    betSumMoney += oldItem.getBet().getBetMoney();
                    betExpMoney += oldItem.getBet().getExpMoney();
                    betCnt++;
                }

                // 축베팅은 2번까지만 허용
                if (betCnt > gameConfig.getSportsBetCnt()) {
                    result.setMessage("규정상 축베팅은 " + gameConfig.getSportsBetCnt() + "번까지만 허용 됩니다.");
                    return result;
                }

                if (betSumMoney + bet.getBetMoney() > gameConfig.getMax()) {
                    result.setMessage("베팅된 게임중 베팅금액 합이 " + (gameConfig.getMax() / 10000) + "만원 넘는 게임이 있습니다.");
                    return result;
                }

                if (betExpMoney + bet.getExpMoney() > gameConfig.getMark()) {
                    result.setMessage("베팅된 게임중 예상 당첨금 합이 " + (gameConfig.getMark() / 10000) + "만원 넘는 게임이 있습니다.");
                    return result;
                }
            }
        }

        // 베팅을 저장한다.
        boolean success = betService.addGameBetting(bet);
        if (success) {
            result.setSuccess(true);
        } else {
            result.setMessage("베팅에 실패하였습니다. 잠시후 다시 시도하세요.");
        }

        return result;
    }

    @Transactional
    @Override
    public void updateGameDate(Long id, Date gameDate) {
        QBetItem q = QBetItem.betItem;
        Iterable<BetItem> betItems = betItemRepository.findAll(q.gameId.eq(id));
        betItems.forEach(x -> x.updateGameDate(gameDate));
        betItemRepository.save(betItems);
    }

    @Override
    public Iterable<BetItem> getBalanceBet(MenuCode menuCode, String sdate) {
        QBetItem q = QBetItem.betItem;
        return betItemRepository.findAll(q.groupId.eq(sdate).and(q.menuCode.eq(menuCode)).and(q.role.eq(Role.USER)).and(q.bet.balance.isTrue()));
    }

}
