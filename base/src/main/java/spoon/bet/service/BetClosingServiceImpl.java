package spoon.bet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.bet.entity.QBetItem;
import spoon.bet.repository.BetItemRepository;
import spoon.bet.repository.BetRepository;
import spoon.config.domain.Config;
import spoon.game.domain.GameCode;
import spoon.game.domain.GameResult;
import spoon.gameZone.ZoneScore;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BetClosingServiceImpl implements BetClosingService {

    private BetItemRepository betItemRepository;

    private BetRepository betRepository;

    private MemberService memberService;

    private PaymentService paymentService;

    private static QBetItem q = QBetItem.betItem;

    @Override
    public boolean notBetting(long gameId) {
        QBetItem q = QBetItem.betItem;
        return betItemRepository.count(q.gameId.eq(gameId).and(q.gameCode.ne(GameCode.ZONE)).and(q.cancel.isFalse()).and(q.role.in(Role.USER, Role.AGENCY1, Role.AGENCY2, Role.AGENCY3, Role.AGENCY4))) == 0;
    }

    @Transactional
    @Override
    public void closingGameBetting(Long gameId, Integer scoreHome, Integer scoreAway, boolean cancel) {

        Iterable<BetItem> betItems = betItemRepository.findAll(q.gameId.eq(gameId).and(q.cancel.isFalse()));
        for (BetItem item : betItems) {
            Bet bet = item.getBet();

            if (item.isClosing()) continue; // 이미 결과처리 된 베팅아이템이다

            boolean closing = item.updateScore(scoreHome, scoreAway, cancel);
            if (!closing) continue;

            bet.setPayment(true);
            bet.setClosing(true);
            bet.setClosingDate(new Date());
            betRepository.saveAndFlush(bet);

            if (bet.getRole() != Role.USER) continue; // 더미는 해 줄것이 없다.

            if (bet.getHitMoney() > 0) {
                paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), makeGameMemo(bet, "적중"));
                // 추천인 지급 폴더보다 폴더가 많고 베팅시 지급대상이라면
                if (bet.getBetCount() >= Config.getGameConfig().getRecommFolder() && Config.getGameConfig().isRecommType()) { // 베팅시 베팅금액 지정되어 있다면
                    paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeGameMemo(bet, "추천인"), true);
                }
            } else {
                // 미적중시 미적중 포인트 확인
                if (bet.getBetCount() >= Config.getGameConfig().getNoHitFolder()) {
                    paymentLose(bet.getId(), bet.getUserid(), bet.getLevel(), bet.getBetMoney(), makeGameMemo(bet, "미적중"), true);
                }
                // 미적중시 추천인 확인
                if (bet.getBetCount() >= Config.getGameConfig().getRecommFolder()) {
                    paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeGameMemo(bet, "추천인"), true);
                }
            }
        }
    }

    @Transactional
    @Override
    public void rollbackBetting(Long gameId) {
        QBetItem qi = QBetItem.betItem;
        Iterable<BetItem> betItems = betItemRepository.findAll(qi.gameId.eq(gameId).and(qi.gameCode.ne(GameCode.ZONE)));
        rollbackBetting(betItems);
    }

    @Transactional
    @Override
    public void rollbackBetting(Iterable<BetItem> betItems) {
        for (BetItem item : betItems) {
            // 회원이 아니라면 그냥 넘어간다
            if (item.getRole() != Role.USER) continue;

            // 베팅취소한 경기는 그냥 넘어 간다.
            if (item.isCancel() && item.getGameResult() != GameResult.CANCEL) {
                continue;
            }

            Bet bet = item.getBet();
            if (bet.isPayment()) {
                if (bet.getHitMoney() > 0) {
                    paymentService.rollbackMoney(MoneyCode.WIN, item.getBet().getId(), item.getBet().getUserid());
                } else {
                    paymentService.rollbackPoint(PointCode.LOSE, item.getBet().getId(), item.getBet().getUserid());
                }
                paymentService.rollbackRecomm(PointCode.RECOMM, item.getBet().getId(), item.getBet().getUserid());
            }

            item.reset();
            if (bet.isPayment()) bet.setCancel(false);
            bet.setClosing(false);
            bet.setPayment(false);
            bet.setHitMoney(0);
            betRepository.saveAndFlush(bet);
        }
    }

    @Transactional
    @Override
    public void closingZoneBetting(Bet bet, ZoneScore zoneScore) {
        BetItem item = bet.getBetItems().get(0);

        item.updateScore(zoneScore);
        bet.setPayment(true);
        bet.setClosing(true);
        bet.setClosingDate(new Date());

        betRepository.saveAndFlush(bet);

        // 더미회원은 해줄것이 없다.
        if (bet.getRole() == Role.DUMMY) return;

        if (bet.getHitMoney() > 0) {
            paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), makeZoneMemo(item, "적중"));
            if (Config.getGameConfig().isRecommType()) { // 베팅시 베팅금액 지정되어 있다면
                paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeZoneMemo(item, "추천인"), false);
            }
        } else {
            // 미적중시 미적중 포인트 확인
            paymentLose(bet.getId(), bet.getUserid(), bet.getLevel(), bet.getBetMoney(), makeZoneMemo(item, "미적중"), false);
            // 미적중시 추천인 확인
            paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeZoneMemo(item, "추천인"), false);
        }
    }

    private void paymentRecomm(Long betId, String userid, long betMoney, String memo, boolean sports) {
        // 추천인 미적중 포인트가 지급이 아니라면
        if (!Config.getGameConfig().isRecommPayment()) return;

        User recomm = memberService.getRecomm(userid);
        if (recomm != null) {
            // 스포츠와 게임존 구분
            double recommRate = sports ? Config.getGameConfig().getNoHitSportsRecommOdds()[recomm.getLevel()] : Config.getGameConfig().getNoHitZoneRecommOdds()[recomm.getLevel()];
            if (recommRate > 0) {
                long recommPoint = (long) (betMoney * recommRate / 100);
                if (paymentService.notPaidPoint(PointCode.RECOMM, betId, recomm.getUserid())) {
                    paymentService.addPoint(PointCode.RECOMM, betId, recomm.getUserid(), recommPoint, memo);
                }
            }
        }
    }

    private void paymentLose(Long betId, String userid, int level, long betMoney, String memo, boolean sports) {
        // 스포츠와 게임존 구분
        double rate = sports ? Config.getGameConfig().getNoHitSportsOdds()[level] : Config.getGameConfig().getNoHitZoneOdds()[level];
        if (rate > 0) {
            long losePoint = (long) (betMoney * rate / 100);
            if (paymentService.notPaidPoint(PointCode.LOSE, betId, userid)) {
                paymentService.addPoint(PointCode.LOSE, betId, userid, losePoint, memo);
            }
        }
    }

    private void paymentWin(Long betId, String userid, long hitMoney, String memo) {
        if (paymentService.notPaidMoney(MoneyCode.WIN, betId, userid)) {
            paymentService.addMoney(MoneyCode.WIN, betId, userid, hitMoney, memo);
        }
    }

    private String makeGameMemo(Bet bet, String result) {
        return bet.getMenuCode().getName() + " " + bet.getBetCount() + "폴더 " + result + " " + bet.getBetMoney() + "원베팅";
    }

    private String makeZoneMemo(BetItem item, String result) {
        return item.getMenuCode().getName() + " " + item.getLeague() + " " + item.getSpecial() + " " + result;
    }
}
