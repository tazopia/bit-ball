package spoon.bet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.bet.repository.BetRepository;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.domain.GameResult;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class BetResultService {

    private final BetRepository betRepository;

    private final PaymentService paymentService;

    private final MemberService memberService;

    /**
     * 적중처리
     */
    @Transactional
    public AjaxResult win(Long id) {
        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "선택한 베팅을 적중처리 할 수 없습니다.");

        Bet bet = betRepository.findOne(id);

        if (bet.isCancel()) {
            return new AjaxResult(false, "취소처리 된 베팅은 적중처리 할 수 없습니다.");
        }

        if (bet.isClosing()) { // 이미 처리된 베팅이다.
            if ("적중".equals(bet.getResult())) {
                return new AjaxResult(false, "이미 적중처리 되었습니다.");
            }

            if (bet.isPayment()) {
                paymentService.rollbackMoney(MoneyCode.WIN, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.LOSE, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.RECOMM, bet.getId(), bet.getUserid());
            }
        }

        bet.getBetItems().forEach(x -> {
            if ("home".equals(x.getBetTeam())) {
                x.setScoreHome(1);
                x.setScoreAway(0);
                x.setGameResult(GameResult.HOME);
            } else if ("away".equals(x.getBetTeam())) {
                x.setScoreHome(0);
                x.setScoreAway(1);
                x.setGameResult(GameResult.AWAY);
            } else {
                x.setScoreHome(1);
                x.setScoreAway(1);
                x.setGameResult(GameResult.DRAW);
            }
            x.setResult("적중");
            x.setCancel(false);
            x.setClosing(true);
        });
        bet.setCancel(false);
        bet.setClosing(true);
        bet.setClosingDate(new Date());
        bet.setResult("적중");
        bet.setLoseCount(0);
        bet.setHitCount(bet.getBetItems().size());
        bet.setCancelCount(0);
        bet.setPayment(true);
        bet.setHitOdds(bet.getBetOdds());
        bet.setHitMoney(bet.getExpMoney());

        betRepository.saveAndFlush(bet);
        if (bet.getRole() == Role.DUMMY) return new AjaxResult(true, "베팅을 적중처리 하였습니다."); // 더미는 해 줄것이 없다.

        paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), makeGameMemo(bet, "적중"));
        // 추천인 지급 폴더보다 폴더가 많고 베팅시 지급대상이라면
        if (bet.getBetCount() >= Config.getGameConfig().getRecommFolder() && Config.getGameConfig().isRecommType()) { // 베팅시 베팅금액 지정되어 있다면
            paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeGameMemo(bet, "추천인"), true);
        }

        return new AjaxResult(true, "베팅을 적중처리 하였습니다.");
    }

    /**
     * 적특 처리
     */
    @Transactional
    public AjaxResult hit(Long id) {
        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "선택한 베팅을 적특처리 할 수 없습니다.");

        Bet bet = betRepository.findOne(id);

        if (bet.isCancel()) {
            return new AjaxResult(false, "취소처리 된 베팅은 적특처리 할 수 없습니다.");
        }

        if (bet.isClosing()) { // 이미 처리된 베팅이다.
            if ("적특".equals(bet.getResult())) {
                return new AjaxResult(false, "이미 적특처리 되었습니다.");
            }

            if (bet.isPayment()) {
                paymentService.rollbackMoney(MoneyCode.WIN, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.LOSE, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.RECOMM, bet.getId(), bet.getUserid());
            }
        }

        bet.getBetItems().forEach(x -> {
            x.setScoreHome(0);
            x.setScoreAway(0);
            x.setGameResult(GameResult.HIT);
            x.setResult("적특");
            x.setCancel(false);
            x.setClosing(true);
        });
        bet.setCancel(false);
        bet.setClosing(true);
        bet.setClosingDate(new Date());
        bet.setResult("적특");
        bet.setLoseCount(0);
        bet.setHitCount(0);
        bet.setCancelCount(bet.getBetItems().size());
        bet.setPayment(true);
        bet.setHitOdds(1D);
        bet.setHitMoney(bet.getBetMoney());

        betRepository.saveAndFlush(bet);
        if (bet.getRole() == Role.DUMMY) return new AjaxResult(true, "베팅을 적특처리 하였습니다."); // 더미는 해 줄것이 없다.

        paymentWin(bet.getId(), bet.getUserid(), bet.getHitMoney(), makeGameMemo(bet, "적중"));
        // 추천인 지급 폴더보다 폴더가 많고 베팅시 지급대상이라면
        if (bet.getBetCount() >= Config.getGameConfig().getRecommFolder() && Config.getGameConfig().isRecommType()) { // 베팅시 베팅금액 지정되어 있다면
            paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeGameMemo(bet, "추천인"), true);
        }

        return new AjaxResult(true, "베팅을 적특처리 하였습니다.");
    }

    /**
     * 미적 처리
     */
    @Transactional
    public AjaxResult lose(Long id) {
        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "선택한 베팅을 미적처리 할 수 없습니다.");

        Bet bet = betRepository.findOne(id);

        if (bet.isCancel()) {
            return new AjaxResult(false, "취소처리 된 베팅은 미적처리 할 수 없습니다.");
        }

        if (bet.isClosing()) { // 이미 처리된 베팅이다.
            if ("미적중".equals(bet.getResult())) {
                return new AjaxResult(false, "이미 미적처리 되었습니다.");
            }

            if (bet.isPayment()) {
                paymentService.rollbackMoney(MoneyCode.WIN, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.LOSE, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.RECOMM, bet.getId(), bet.getUserid());
            }
        }

        bet.getBetItems().forEach(x -> {
            if ("home".equals(x.getBetTeam())) {
                x.setScoreHome(0);
                x.setScoreAway(1);
                x.setGameResult(GameResult.AWAY);
            } else if ("away".equals(x.getBetTeam())) {
                x.setScoreHome(1);
                x.setScoreAway(0);
                x.setGameResult(GameResult.HOME);
            } else {
                x.setScoreHome(1);
                x.setScoreAway(0);
                x.setGameResult(GameResult.HOME);
            }
            x.setResult("미적중");
            x.setCancel(false);
            x.setClosing(true);
        });
        bet.setCancel(false);
        bet.setClosing(true);
        bet.setClosingDate(new Date());
        bet.setResult("미적중");
        bet.setLoseCount(bet.getBetItems().size());
        bet.setHitCount(0);
        bet.setCancelCount(0);
        bet.setPayment(true);
        bet.setHitOdds(0);
        bet.setHitMoney(0);

        betRepository.saveAndFlush(bet);
        if (bet.getRole() == Role.DUMMY) return new AjaxResult(true, "베팅을 미적처리 하였습니다."); // 더미는 해 줄것이 없다.

        // 미적중시 미적중 포인트 확인
        if (bet.getBetCount() >= Config.getGameConfig().getNoHitFolder()) {
            paymentLose(bet.getId(), bet.getUserid(), bet.getLevel(), bet.getBetMoney(), makeGameMemo(bet, "미적중"), true);
        }
        // 미적중시 추천인 확인
        if (bet.getBetCount() >= Config.getGameConfig().getRecommFolder()) {
            paymentRecomm(bet.getId(), bet.getUserid(), bet.getBetMoney(), makeGameMemo(bet, "추천인"), true);
        }

        return new AjaxResult(true, "베팅을 미적처리 하였습니다.");
    }

    @Transactional
    public AjaxResult cancel(Long id) {
        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");

        Bet bet = betRepository.findOne(id);

        if (bet.isCancel()) {
            return new AjaxResult(false, "이미 취소된 베팅입니다.");
        }

        boolean payment = bet.isPayment();
        bet.allCancel();
        betRepository.saveAndFlush(bet);

        if (payment) {
            paymentService.rollbackMoney(MoneyCode.WIN, bet.getId(), bet.getUserid());
            paymentService.rollbackPoint(PointCode.LOSE, bet.getId(), bet.getUserid());
            paymentService.rollbackPoint(PointCode.RECOMM, bet.getId(), bet.getUserid());
        }

        MoneyCode moneyCode = MoneyCode.BET_ZONE;
        paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getUserid());

        // 롤링이 있는지 확인한다.
        moneyCode = MoneyCode.ROLL_ZONE;
        paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getUserid());

        if (StringUtils.notEmpty(bet.getAgency1()))
            paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency1());
        if (StringUtils.notEmpty(bet.getAgency2()))
            paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency2());
        if (StringUtils.notEmpty(bet.getAgency3()))
            paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency3());
        if (StringUtils.notEmpty(bet.getAgency4()))
            paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency4());

        return new AjaxResult(true, "베팅을 취소하였습니다.");
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

    private String makeGameMemo(Bet bet, String result) {
        return bet.getMenuCode().getName() + " " + bet.getBetCount() + "폴더 " + result + " " + bet.getBetMoney() + "원베팅";
    }

    private String makeZoneMemo(BetItem item, String result) {
        return item.getMenuCode().getName() + " " + item.getLeague() + " " + item.getSpecial() + " " + result;
    }
}
