package spoon.gameZone.bitcoin1.service;

import lombok.RequiredArgsConstructor;
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
import spoon.common.utils.WebUtils;
import spoon.game.domain.MenuCode;
import spoon.gameZone.Zone;
import spoon.gameZone.ZoneConfig;
import spoon.gameZone.ZoneDto;
import spoon.gameZone.ZoneScore;
import spoon.gameZone.bitcoin1.domain.Bitcoin1Config;
import spoon.gameZone.bitcoin1.entity.Bitcoin1;
import spoon.gameZone.bitcoin1.entity.QBitcoin1;
import spoon.gameZone.bitcoin1.repository.Bitcoin1Repository;
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
@RequiredArgsConstructor
@Service
public class Bitcoin1GameService {

    private final Bitcoin1Repository bitcoin1Repository;

    private final BetClosingService betClosingService;

    private final BetItemRepository betItemRepository;

    private final BetService betService;

    private final MemberService memberService;

    private final BetMapper betMapper;

    private static QBitcoin1 q = QBitcoin1.bitcoin1;

    @Transactional
    public void closingBetting(Bitcoin1 bitcoin1) {
        QBetItem qi = QBetItem.betItem;
        Iterable<BetItem> betItems = betItemRepository.findAll(qi.menuCode.eq(MenuCode.BITCOIN1).and(qi.groupId.eq(bitcoin1.getSdate())).and(qi.cancel.isFalse()));
        for (BetItem item : betItems) {
            Bet bet = item.getBet();
            if (bet.isPayment()) continue;

            ZoneScore zoneScore = bitcoin1.getGameResult(item.getSpecial());
            betClosingService.closingZoneBetting(bet, zoneScore);
        }
    }

    @Transactional
    protected void addAmount(String sdate) {
        try {
            long[] amount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            List<BetDto.ZoneAmount> list = betMapper.zoneAmount(MenuCode.BITCOIN1, sdate);
            for (BetDto.ZoneAmount bet : list) {
                amount[bet.getBetZone()] = bet.getAmount();
            }
            Bitcoin1 entity = bitcoin1Repository.findOne(q.sdate.eq(sdate));
            entity.setAmount(amount);
            bitcoin1Repository.saveAndFlush(entity);
        } catch (RuntimeException e) {
            log.error("베팅시 게임별 베팅금액 계산에 실패 하였습니다. - {}", e.getMessage(), e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional
    public void rollbackPayment(Bitcoin1 entity) {
        QBetItem qi = QBetItem.betItem;
        Iterable<BetItem> betItems = betItemRepository.findAll(qi.menuCode.eq(MenuCode.BITCOIN1).and(qi.groupId.eq(entity.getSdate())).and(qi.cancel.isFalse()));
        betClosingService.rollbackBetting(betItems);
    }

    @Transactional
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

        Bitcoin1Config config = ZoneConfig.getBitcoin1();
        Bitcoin1 entity = bitcoin1Repository.findOne(q.sdate.eq(betting.getSdate()));
        Zone zone = entity.getZone(betting.getGameCode());
        double odds = entity.getOdds()[betting.getBetZone()];

        if (zone == null) {
            result.setMessage("베팅에 실패하였습니다.");
            return result;
        }

        if (odds != betting.getOdds()) {
            result.setMessage("비트코인 1분차트 배당이 변경되었습니다. 페이지를 리로딩 하여 주세요.");
            return result;
        }

        // 배당이 정확한지
        if (odds < 1) {
            result.setMessage("비트코인 1분차트 베팅에 실패하였습니다. 다시 베팅하여 주세요.");
            return result;
        }

        // 베팅시간 체크
        if (entity.getGameDate().getTime() - (60 * 1000) - config.getBetTime() * 1000 < System.currentTimeMillis()) {
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
        long count = betItemRepository.count(qi.userid.eq(userid).and(qi.menuCode.eq(MenuCode.BITCOIN1)).and(qi.groupId.eq(entity.getSdate())).and(qi.cancel.isFalse()));
        if (count >= config.getBetMax()) {
            result.setMessage(String.format("비트코인 1분 차트는 회차당 %d 회만 베팅 가능 합니다.", config.getBetMax()));
            return result;
        }

        // 동일조합 체크
        if (config.getBetMax() > 1) {
            count = betItemRepository.count(qi.userid.eq(userid).and(qi.menuCode.eq(MenuCode.BITCOIN1)).and(qi.groupId.eq(entity.getSdate())).and(qi.special.eq(betting.getGameCode())).and(qi.cancel.isFalse()));
            if (count > 0) {
                result.setMessage("비트코인 1분 차트는 동일 게임을 중복 베팅할 수 없습니다.");
                return result;
            }
        }

        List<BetItem> betItems = new ArrayList<>();
        BetItem betItem = new BetItem(zone, userid, member.getRole(), betting.getBetTeam(), betting.getBetZone(), betting.getBetMoney(), odds);
        betItems.add(betItem);

        Bet bet = new Bet(member.getUser());
        bet.setBetMoney(betting.getBetMoney());
        bet.setBetItems(betItems);
        bet.setMenuCode(MenuCode.BITCOIN1);
        bet.setBetCount(1);
        bet.setBetDate(new Date());
        bet.setBetOdds(odds);
        bet.setStartDate(entity.getGameDate());
        bet.setEndDate(entity.getGameDate());
        bet.setExpMoney((long) (bet.getBetOdds() * betting.getBetMoney()));
        bet.setIp(WebUtils.ip());

        if (bet.getBetOdds() != betItem.getOdds()) {
            log.error("{}, {}", bet.toString(), betItem.toString());
            result.setMessage("배당이 일치하지 않습니다. 다시 베팅하여 주세요.");
            return result;
        }


        boolean success = betService.addZoneBetting(bet);

        if (success) {
            result.setSuccess(true);
            if (bet.getRole() != Role.DUMMY) {
                addAmount(entity.getSdate());
            }
        } else {
            result.setMessage("베팅에 실패하였습니다. 잠시후 다시 시도하세요.");
        }

        return result;
    }
}
