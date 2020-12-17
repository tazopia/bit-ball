package spoon.banking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.Rolling;
import spoon.banking.entity.Banking;
import spoon.banking.entity.QBanking;
import spoon.banking.repository.BankingRepository;
import spoon.common.utils.DateUtils;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.event.service.DailyEventService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PointCode;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BankingServiceImpl implements BankingService {

    private PaymentService paymentService;

    private DailyEventService dailyEventService;

    private BankingRepository bankingRepository;

    private static QBanking q = QBanking.banking;

    @Override
    public Banking getBanking(long id) {
        return bankingRepository.findOne(id);
    }

    @Override
    public boolean existWorkBanking(String userid) {
        return bankingRepository.count(q.userid.eq(userid).and(q.cancel.isFalse()).and(q.closing.isFalse())) > 0;
    }

    @Transactional
    @Override
    public boolean addDeposit(Banking banking) {
        try {
            bankingRepository.saveAndFlush(banking);
            return true;
        } catch (RuntimeException e) {
            log.error("충전신청을 저장하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional
    @Override
    public boolean cancelDeposit(long id) {
        try {
            Banking banking = getBanking(id);
            if (banking.isClosing() || banking.isCancel()) {
                throw new RuntimeException();
            }
            banking.setCancel(true);
            banking.setClosing(true);
            banking.setClosingDate(new Date());
            bankingRepository.saveAndFlush(banking);
            return true;
        } catch (RuntimeException e) {
            log.error("충전신청 취소를 하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional
    @Override
    public boolean submitDeposit(long id) {
        try {
            Banking banking = getBanking(id);

            if (banking.isCancel() || banking.isClosing() || banking.isReset()) {
                throw new BankingException("이미 취소 되었거나 완료처리가 되었습니다.");
            }

            banking.setClosing(true);
            banking.setClosingDate(new Date());
            banking.setWorker(WebUtils.userid());
            bankingRepository.saveAndFlush(banking);

            paymentService.addMoney(MoneyCode.DEPOSIT, id, banking.getUserid(), banking.getAmount(), banking.getBonus());
            if (banking.getBonusPoint() > 0) {
                paymentService.addPoint(PointCode.DEPOSIT, id, banking.getUserid(), banking.getBonusPoint(), banking.getBonus());
            }

            // 출석체크 이벤트
            if (Config.getSysConfig().getEvent().isDaily()) {
                dailyEventService.checkEvent(banking.getUserid());
            }

            return true;
        } catch (RuntimeException e) {
            log.error("충전신청 완료 처리를 하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional
    @Override
    public AjaxResult rollbackDeposit(long id) {
        try {
            Banking banking = getBanking(id);

            if (banking.isCancel()) {
                throw new BankingException("이미 취소된 충전신청 입니다.");
            }

            if (!banking.isClosing()) { // 클로징이 안 되었다는 것은 입금 처리가 되지 않았다는 것이다.
                throw new BankingException("입금 완료 처리가 되지 않았습니다.");
            }

            if (banking.isReset()) {
                throw new BankingException("이미 충전취소 - 포인트 복원된 충전요청 입니다.");
            }

            banking.setCancel(true);
            banking.setReset(true);
            banking.setClosingDate(new Date());
            bankingRepository.saveAndFlush(banking);

            paymentService.addMoney(MoneyCode.DEPOSIT_ROLLBACK, id, banking.getUserid(), -banking.getAmount(), banking.getBonus());
            if (banking.getBonusPoint() > 0) {
                paymentService.addPoint(PointCode.DEPOSIT_ROLLBACK, id, banking.getUserid(), -banking.getBonusPoint(), banking.getBonus());
            }
            return new AjaxResult(true, "충전취소 - 포인트 복원을 완료 하였습니다.");
        } catch (RuntimeException e) {
            log.error("충전취소를 롤백하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }
    }

    @Override
    public boolean existBeforeMinutesWithdraw(String userid, int minutes) {
        return bankingRepository.exists(
                q.userid.eq(userid)
                        .and(q.bankingCode.eq(BankingCode.OUT))
                        .and(q.cancel.isFalse())
                        .and(q.closing.isTrue())
                        .and(q.closingDate.after(DateUtils.beforeMinutes(minutes)))
        );
    }

    @Transactional
    @Override
    public boolean addWithdraw(Banking banking) {
        try {
            bankingRepository.saveAndFlush(banking);
            paymentService.addMoney(MoneyCode.WITHDRAW, banking.getId(), banking.getUserid(), -banking.getAmount(), getRolling(banking.getRolling()));
            return true;
        } catch (RuntimeException e) {
            log.error("환전 신청을 하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional
    @Override
    public boolean addSellerWithdraw(Banking banking) {
        try {
            bankingRepository.saveAndFlush(banking);
            paymentService.addPoint(PointCode.WITHDRAW, banking.getId(), banking.getUserid(), -banking.getAmount(), banking.getRole().getName() + " 출금 신청");
            return true;
        } catch (RuntimeException e) {
            log.error("총판 출금 신청을 하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional
    @Override
    public boolean submitWithdraw(Banking banking) {
        try {
            if (banking.isCancel() || banking.isClosing() || banking.isReset()) {
                throw new BankingException("이미 취소 되었거나 완료처리가 되었습니다.");
            }

            banking.setWorker(WebUtils.userid());
            banking.setClosing(true);
            banking.setClosingDate(new Date());
            bankingRepository.saveAndFlush(banking);

            return true;
        } catch (RuntimeException e) {
            log.error("환전신청 완료 처리를 하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public AjaxResult rollbackWithdraw(long id) {
        try {
            Banking banking = getBanking(id);

            if (banking.isCancel()) {
                throw new BankingException("이미 취소된 환전신청 입니다.");
            }

            if (banking.isReset()) {
                throw new BankingException("이미 취소된 환전신청 입니다.");
            }

            banking.setCancel(true);
            banking.setReset(true);
            banking.setClosing(true);
            banking.setClosingDate(new Date());
            bankingRepository.saveAndFlush(banking);

            paymentService.addMoney(MoneyCode.WITHDRAW_ROLLBACK, id, banking.getUserid(), banking.getAmount(), getRolling(banking.getRolling()));

            return new AjaxResult(true, "환전취소 - 포인트 복원을 완료 하였습니다.");
        } catch (RuntimeException e) {
            log.error("환전신청 취소 (롤백) 처리를 하지 못하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }
    }


    @Transactional
    @Override
    public boolean delete(long id) {
        try {
            Banking banking = getBanking(id);
            banking.setHidden(true);
            bankingRepository.saveAndFlush(banking);
            return true;
        } catch (RuntimeException e) {
            log.error("충환전 내역 삭제에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional
    @Override
    public void stop(long id) {
        try {
            Banking banking = getBanking(id);
            banking.setAlarm(false);
            bankingRepository.saveAndFlush(banking);
        } catch (RuntimeException e) {
            log.error("충환전 대기에 실패하였습니다. - {}", e.getMessage());
            log.warn("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    public boolean isFirstDeposit(String userid) {
        String today = DateUtils.todayString();
        return bankingRepository.count(q.userid.eq(userid)
                .and(q.regDate.goe(DateUtils.start(today))).and(q.regDate.lt(DateUtils.end(today)))
                .and(q.bankingCode.eq(BankingCode.IN))
                .and(q.cancel.isFalse()).and(q.closing.isTrue())) == 0;
    }

    @Override
    public boolean isEveryDeposit(String userid) {
        // 환전이 있으면 매충이 아니다.
        String today = DateUtils.todayString();
        return bankingRepository.count(q.userid.eq(userid)
                .and(q.regDate.goe(DateUtils.start(today))).and(q.regDate.lt(DateUtils.end(today)))
                .and(q.bankingCode.eq(BankingCode.OUT))
                .and(q.cancel.isFalse()).and(q.closing.isTrue())) == 0;
    }

    @Override
    public long getTodayDeposit(String userid) {
        return bankingRepository.getTodayDeposit(userid);
    }

    private String getRolling(Rolling rolling) {
        return "스포츠: " + rolling.getRollingSports() + "%, 게임존: " + rolling.getRollingZone() + "%";
    }
}
