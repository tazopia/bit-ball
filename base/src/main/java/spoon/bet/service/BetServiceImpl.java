package spoon.bet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.bet.domain.BetDto;
import spoon.bet.entity.Bet;
import spoon.bet.entity.BetItem;
import spoon.bet.entity.QBet;
import spoon.bet.entity.QBetItem;
import spoon.bet.repository.BetItemRepository;
import spoon.bet.repository.BetRepository;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.game.domain.MenuCode;
import spoon.mapper.BetMapper;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.service.MemberService;
import spoon.monitor.service.MonitorService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BetServiceImpl implements BetService {

    private ApplicationEventPublisher eventPublisher;

    private MemberService memberService;

    private PaymentService paymentService;

    private MonitorService monitorService;

    private BetRepository betRepository;

    private BetItemRepository betItemRepository;

    private BetMapper betMapper;

    @Transactional
    @Override
    public boolean addGameBetting(Bet bet) {
        try {
            long betMoney = bet.getBetMoney();
            String memo = bet.getMenuCode().getName() + " " + bet.getBetCount() + "폴더 베팅";

            // 먼저 betting을 저장하여 betId를 만든다.
            betRepository.saveAndFlush(bet);

            // 게시판 회원이 아닐 경우만
            if (bet.getRole() != Role.DUMMY) {
                updateGameAmount(bet.getBetItems().stream().mapToLong(BetItem::getGameId).toArray());
                paymentService.addMoney(MoneyCode.BET_SPORTS, bet.getId(), bet.getUserid(), -betMoney, memo);
            }
        } catch (Exception e) {
            log.error("스포츠 베팅에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        if (bet.getRole() == Role.USER) {
            monitorService.checkBlack();
        }
        return true;
    }

    @Transactional
    @Override
    public long updateGameAmount(long... gameIds) {
        try {
            return betMapper.updateGameAmount(gameIds);
        } catch (RuntimeException e) {
            log.error("게임에 베팅된 금액 등록에 실패 하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
    }

    @Transactional
    @Override
    public boolean addZoneBetting(Bet bet) {
        try {
            BetItem item = bet.getBetItems().get(0);
            long betMoney = bet.getBetMoney();
            String memo = item.getSports() + " " + item.getLeague() + " " + item.getSpecial() + " " + bet.getBetMoney() + "원 베팅";

            // 먼저 betting을 저장하여 betId를 만든다.
            betRepository.saveAndFlush(bet);

            // 게시판 회원이 아닐 경우만
            if (bet.getRole() != Role.DUMMY) {
                eventPublisher.publishEvent(bet);
                paymentService.addMoney(MoneyCode.BET_ZONE, bet.getId(), bet.getUserid(), -betMoney, memo);
            }
        } catch (RuntimeException e) {
            log.error("게임존 베팅에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        if (bet.getRole() != Role.DUMMY) {
            monitorService.checkBlack();
        }

        return true;
    }

    @Transactional
    @Override
    public AjaxResult cancelUser(Long id) {
        QBet q = QBet.bet;

        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");

        Bet bet = betRepository.findOne(id);
        if (bet == null || !userid.equals(bet.getUserid()) || !bet.isCanCancel() || bet.isClosing()) {
            return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");
        }

        if (bet.isCancel()) {
            return new AjaxResult(false, "이미 취소된 베팅입니다.");
        }

        String today = DateUtils.todayString();
        long count = betRepository.count(q.userid.eq(userid)
                .and(q.betDate.goe(DateUtils.start(today))).and(q.betDate.lt(DateUtils.end(today)))
                .and(q.cancel.isTrue()));
        if (count >= Config.getGameConfig().getCancelMax()) {
            return new AjaxResult(false, "하루 최대 " + Config.getGameConfig().getCancelMax() + "회까지 취소 가능합니다.");
        }

        return cancel(bet);
    }

    @Transactional
    @Override
    public AjaxResult cancelBet(Long id) {
        String userid = WebUtils.userid();
        if (userid == null) return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");

        Bet bet = betRepository.findOne(id);
        if (bet == null || bet.isClosing()) {
            return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");
        }

        if (bet.isCancel()) {
            return new AjaxResult(false, "이미 취소된 베팅입니다.");
        }
        return cancel(bet);
    }

    private AjaxResult cancel(Bet bet) {
        try {
            boolean payment = bet.isPayment();
            long[] gameIds = bet.allCancel();
            betRepository.saveAndFlush(bet);
            updateGameAmount(gameIds);

            if (payment) {
                paymentService.rollbackMoney(MoneyCode.WIN, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.LOSE, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.RECOMM, bet.getId(), bet.getUserid());
            }

            MoneyCode moneyCode = MenuCode.isSports(bet.getMenuCode()) ? MoneyCode.BET_SPORTS : MoneyCode.BET_ZONE;
            paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getUserid());

            // 롤링이 있는지 확인한다.
            moneyCode = MenuCode.isSports(bet.getMenuCode()) ? MoneyCode.ROLL_SPORTS : MoneyCode.ROLL_ZONE;
            paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getUserid());

            if (StringUtils.notEmpty(bet.getAgency1()))
                paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency1());
            if (StringUtils.notEmpty(bet.getAgency2()))
                paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency2());
            if (StringUtils.notEmpty(bet.getAgency3()))
                paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency3());
            if (StringUtils.notEmpty(bet.getAgency4()))
                paymentService.rollbackMoney(moneyCode, bet.getId(), bet.getAgency4());


        } catch (RuntimeException e) {
            log.error("사용자 베팅취소에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");
        }

        monitorService.checkBlack();
        return new AjaxResult(true, "선택한 베팅을 취소하였습니다.");
    }


    @Transactional
    @Override
    public AjaxResult cancelItem(Long betId, Long itemId) {
        Bet bet = betRepository.findOne(betId);
        if (bet == null) {
            return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");
        }

        if (bet.isCancel()) {
            return new AjaxResult(false, "이미 취소된 베팅입니다.");
        }

        MoneyCode moneyCode = MenuCode.isSports(bet.getMenuCode()) ? MoneyCode.BET_SPORTS : MoneyCode.BET_ZONE;

        // 베팅 아이템을 취소함으로 전체 취소가 되는 경우
        long gameId = bet.itemCancel(itemId);
        if (moneyCode == MoneyCode.BET_ZONE || bet.isCancel()) {
            return cancel(bet);
        }

        try {
            if (bet.isPayment()) {
                paymentService.rollbackMoney(MoneyCode.WIN, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.LOSE, bet.getId(), bet.getUserid());
                paymentService.rollbackPoint(PointCode.RECOMM, bet.getId(), bet.getUserid());
            }

            // 한경기를 취소함으로서 전체 경기가 클로징 되었다.
            if (bet.isClosing()) {
                bet.setPayment(true);
                bet.setClosing(true);
                bet.setClosingDate(new Date());

                if (bet.getHitMoney() > 0) {
                    if (paymentService.notPaidMoney(MoneyCode.WIN, bet.getId(), bet.getUserid())) {
                        paymentService.addMoney(MoneyCode.WIN, bet.getId(), bet.getUserid(), bet.getHitMoney(), bet.getMenuCode().getName() + " " + bet.getBetCount() + "폴더 적중");
                    }
                } else {
                    // 미적중시 미적중 포인트 확인
                    double rate = MenuCode.isSports(bet.getMenuCode()) ? Config.getGameConfig().getNoHitSportsOdds()[bet.getLevel()] : Config.getGameConfig().getNoHitZoneOdds()[bet.getLevel()];
                    if (rate > 0) {
                        long losePoint = (long) (bet.getBetMoney() * rate / 100);
                        if (paymentService.notPaidPoint(PointCode.LOSE, bet.getId(), bet.getUserid())) {
                            paymentService.addPoint(PointCode.LOSE, bet.getId(), bet.getUserid(), losePoint, bet.getMenuCode().getName() + " " + bet.getBetCount() + "폴더 미적중");
                        }
                    }

                    // 미적중시 추천인 확인
                    User recomm = memberService.getRecomm(bet.getUserid());
                    if (recomm != null) {
                        double recommRate = MenuCode.isSports(bet.getMenuCode()) ? Config.getGameConfig().getNoHitSportsRecommOdds()[recomm.getLevel()] : Config.getGameConfig().getNoHitZoneRecommOdds()[recomm.getLevel()];
                        if (recommRate > 0) {
                            long recommPoint = (long) (bet.getBetMoney() * recommRate / 100);
                            if (paymentService.notPaidPoint(PointCode.RECOMM, betId, recomm.getUserid())) {
                                paymentService.addPoint(PointCode.RECOMM, betId, recomm.getUserid(), recommPoint, bet.getMenuCode().getName() + " " + bet.getBetCount() + "폴더 미적중");
                            }
                        }
                    }
                }
            }
            betRepository.saveAndFlush(bet);
            updateGameAmount(gameId);

        } catch (RuntimeException e) {
            log.error("관리자 베팅취소에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "선택한 베팅을 취소할 수 없습니다.");
        }

        monitorService.checkBlack();
        return new AjaxResult(true, "선택한 베팅항목을 취소하였습니다.");
    }

    @Override
    public List<BetDto.UserBet> userBetList(String userid) {
        return betMapper.userBetList(userid);
    }

    @Transactional
    @Override
    public AjaxResult delete(BetDto.Delete command) {
        try {
            betMapper.deleteBets(command);
        } catch (RuntimeException e) {
            log.error("사용자 베팅삭제에 실패하였습니다. (delete) - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "베팅 삭제에 실패하였습니다.");
        }
        monitorService.checkBlack();
        return new AjaxResult(true, "선택한 베팅을 삭제하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult deleteClosing(String userid) {
        try {
            betMapper.deleteClosingBets(userid);
        } catch (RuntimeException e) {
            log.error("사용자 베팅삭제에 실패하였습니다. (deleteClosing) - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "베팅 삭제에 실패하였습니다.");
        }
        monitorService.checkBlack();
        return new AjaxResult(true, "마감된 베팅내역을 모두 삭제하였습니다.");
    }

    @Transactional
    @Override
    public AjaxResult removeBlack(Long betId) {
        Bet bet = betRepository.findOne(betId);
        bet.setBlack(false);
        betRepository.saveAndFlush(bet);

        monitorService.checkBlack();

        return new AjaxResult(true);
    }

    @Override
    public Iterable<Bet> getCurrentBet(String userid) {
        QBet q = QBet.bet;
        return betRepository.findAll(q.userid.eq(userid).and(q.closing.isFalse()).and(q.cancel.isFalse()));
    }

    @Override
    public Iterable<BetItem> betMoneyByItem(String userid, Long gameId, String betTeam) {
        QBetItem q = QBetItem.betItem;
        return betItemRepository.findAll(q.userid.eq(userid).and(q.gameId.eq(gameId)).and(q.betTeam.eq(betTeam)));
    }
}
