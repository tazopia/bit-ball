package spoon.gameZone.oddeven.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.domain.BetDto;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.bet.entity.QBetItem;
import spoon.bet.repository.BetItemRepository;
import spoon.bet.service.BetClosingService;
import spoon.bet.service.BetService;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.WebUtils;
import spoon.game.domain.MenuCode;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.ZoneScore;
import spoon.gameZone.oddeven.Oddeven;
import spoon.gameZone.oddeven.OddevenConfig;
import spoon.gameZone.oddeven.OddevenRepository;
import spoon.gameZone.oddeven.QOddeven;
import spoon.mapper.BetMapper;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.support.web.AjaxResult;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class OddevenGameServiceImpl implements OddevenGameService {

    private MemberService memberService;

    private BetService betService;

    private OddevenRepository oddevenRepository;

    private BetClosingService betClosingService;

    private BetItemRepository betItemRepository;

    private BetMapper betMapper;

    private static QOddeven q = QOddeven.oddeven1;

    @Override
    public AjaxResult betting(ZoneDto.Bet betting) {
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

        OddevenConfig config = ZoneConfig.getOddeven();
        Oddeven oddeven = oddevenRepository.findOne(q.sdate.eq(betting.getSdate()));
        Zone zone = oddeven.getZone(betting.getGameCode());
        double odds = oddeven.getOdds()[betting.getBetZone()];

        if (zone == null) {
            result.setMessage("베팅에 실패하였습니다.");
            return result;
        }

        if (odds != betting.getOdds()) {
            result.setMessage("홀짝 배당이 변경되었습니다. 페이지를 리로딩 하여 주세요.");
            return result;
        }

        // 배당이 정확한지
        if (odds < 1) {
            result.setMessage("홀짝 베팅에 실패하였습니다. 다시 베팅하여 주세요.");
            return result;
        }

        // 베팅시간 체크
        if (oddeven.getGameDate().getTime() - config.getBetTime() * 1000 < System.currentTimeMillis() - 60000) {
            result.setMessage("베팅시간이 만료된 경기가 있습니다. 다시 베팅하여 주세요.");
            return result;
        }

        // 보유금액 체크
        if (member.getMoney() < betting.getBetMoney() && member.getRole() == Role.USER) {
            result.setMessage("보유머니가 부족합니다.");
            return result;
        }

        // 최소 베팅 금액
        int min = config.getMin()[member.getLevel()];
        if (betting.getBetMoney() < min) {
            result.setMessage("최소 베팅 금액은 " + NumberFormat.getIntegerInstance().format(min) + "원 입니다.");
            return result;
        }

        // 최대 베팅 금액
        int max = config.getMax()[member.getLevel()];
        if (betting.getBetMoney() > max) {
            result.setMessage("최대 베팅 금액은 " + NumberFormat.getIntegerInstance().format(max) + "원 입니다.");
            return result;
        }

        // 최대 적중 금액
        int win = config.getWin()[member.getLevel()];
        if (betting.getOdds() * betting.getBetMoney() > win) {
            result.setMessage("최대 적중 금액은 " + NumberFormat.getIntegerInstance().format(win) + "원 입니다.");
            return result;
        }

        // 중복베팅 체크
        QBetItem qi = QBetItem.betItem;
        long count = betItemRepository.count(qi.userid.eq(userid).and(qi.menuCode.eq(MenuCode.ODDEVEN)).and(qi.groupId.eq(oddeven.getSdate())).and(qi.cancel.isFalse()));
        if (count >= config.getBetMax()) {
            result.setMessage(String.format("홀짝은 회차당 %d 회만 베팅 가능 합니다.", config.getBetMax()));
            return result;
        }

        // 동일조합 체크
        if (config.getBetMax() > 1) {
            count = betItemRepository.count(qi.userid.eq(userid).and(qi.menuCode.eq(MenuCode.ODDEVEN)).and(qi.groupId.eq(oddeven.getSdate())).and(qi.special.eq(betting.getGameCode())).and(qi.cancel.isFalse()));
            if (count > 0) {
                result.setMessage("홀짝은 동일 게임을 중복 베팅할 수 없습니다.");
                return result;
            }
        }

        List<BetItem> betItems = new ArrayList<>();
        BetItem betItem = new BetItem(zone, userid, member.getRole(), betting.getBetTeam(), betting.getBetZone(), betting.getBetMoney(), odds);
        betItems.add(betItem);

        Bet bet = new Bet(member.getUser());
        bet.setBetMoney(betting.getBetMoney());
        bet.setBetItems(betItems);
        bet.setMenuCode(MenuCode.ODDEVEN);
        bet.setBetCount(1);
        bet.setBetDate(new Date());
        bet.setBetOdds(odds);
        bet.setStartDate(oddeven.getGameDate());
        bet.setEndDate(oddeven.getGameDate());
        bet.setExpMoney((long) (bet.getBetOdds() * betting.getBetMoney()));
        bet.setIp(WebUtils.ip());

        boolean success = betService.addZoneBetting(bet);

        if (success) {
            result.setSuccess(true);
            if (bet.getRole() == Role.USER) {
                addAmount(oddeven.getSdate());
            }
        } else {
            result.setMessage("베팅에 실패하였습니다. 잠시후 다시 시도하세요.");
        }

        return result;
    }

    @Transactional
    @Override
    public void rollbackPayment(Oddeven oddeven) {
        QBetItem qi = QBetItem.betItem;
        Iterable<BetItem> betItems = betItemRepository.findAll(qi.menuCode.eq(MenuCode.ODDEVEN).and(qi.groupId.eq(oddeven.getSdate())).and(qi.cancel.isFalse()));
        betClosingService.rollbackBetting(betItems);
    }

    @Transactional
    @Override
    public void closingBetting(Oddeven oddeven) {
        QBetItem qi = QBetItem.betItem;
        Iterable<BetItem> betItems = betItemRepository.findAll(qi.menuCode.eq(MenuCode.ODDEVEN).and(qi.groupId.eq(oddeven.getSdate())).and(qi.cancel.isFalse()));
        for (BetItem item : betItems) {
            Bet bet = item.getBet();
            if (bet.isPayment()) continue;

            ZoneScore zoneScore = oddeven.getGameResult(item.getSpecial());
            betClosingService.closingZoneBetting(bet, zoneScore);
        }
    }

    @Transactional
    protected void addAmount(String sdate) {
        try {
            long[] amount = {0, 0, 0, 0, 0, 0, 0, 0};
            List<BetDto.ZoneAmount> list = betMapper.zoneAmount(MenuCode.ODDEVEN, sdate);
            for (BetDto.ZoneAmount bet : list) {
                amount[bet.getBetZone()] = bet.getAmount();
            }
            Oddeven oddeven = oddevenRepository.findOne(q.sdate.eq(sdate));
            oddeven.setAmount(amount);
            oddevenRepository.saveAndFlush(oddeven);
        } catch (RuntimeException e) {
            log.error("{} 베팅시 게임별 베팅금액 계산에 실패 하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }
}
