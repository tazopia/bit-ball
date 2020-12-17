package spoon.payment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.WebUtils;
import spoon.member.domain.Role;
import spoon.member.domain.User;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.domain.PaymentDto;
import spoon.payment.domain.PointCode;
import spoon.payment.entity.*;
import spoon.payment.repository.AddMoneyRepository;
import spoon.payment.repository.AddPointRepository;
import spoon.payment.repository.MoneyRepository;
import spoon.payment.repository.PointRepository;
import spoon.support.web.AjaxResult;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private MemberService memberService;

    private MoneyRepository moneyRepository;

    private PointRepository pointRepository;

    private AddMoneyRepository addMoneyRepository;

    private AddPointRepository addPointRepository;

    private static QMoney qm = QMoney.money;
    private static QPoint qp = QPoint.point;

    @Override
    public boolean notPaidMoney(MoneyCode moneyCode, long actionId, String userid) {
        return moneyRepository.count(qm.moneyCode.eq(moneyCode).and(qm.actionId.eq(actionId)).and(qm.userid.eq(userid)).and(qm.cancel.isFalse())) == 0;
    }

    @Override
    public boolean notPaidPoint(PointCode pointCode, long actionId, String userid) {
        return pointRepository.count(qp.pointCode.eq(pointCode).and(qp.actionId.eq(actionId)).and(qp.userid.eq(userid)).and(qp.cancel.isFalse())) == 0;
    }

    @Transactional
    @Override
    public AjaxResult addMoney(PaymentDto.Add add) {
        if (add.getAmount() == 0) {
            return new AjaxResult(false, "지급할 머니가 없습니다.");
        }

        MoneyCode moneyCode = add.isPlus() ? MoneyCode.ADD : MoneyCode.REMOVE;
        if (!add.isPlus()) add.setAmount(-add.getAmount());

        try {
            addMoney(moneyCode, 0L, add.getUserid(), add.getAmount(), add.getMemo());
        } catch (RuntimeException e) {
            log.error("관리자 머니 지급에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "관리자 머니 지급에 실패하였습니다.");
        }
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult addPoint(PaymentDto.Add add) {
        if (add.getAmount() == 0) {
            return new AjaxResult(false, "지급할 포인트가 없습니다.");
        }

        PointCode pointCode = add.isPlus() ? PointCode.ADD : PointCode.REMOVE;
        if (!add.isPlus()) add.setAmount(-add.getAmount());

        try {
            addPoint(pointCode, 0L, add.getUserid(), add.getAmount(), add.getMemo());
        } catch (RuntimeException e) {
            log.error("관리자 포인트 지급에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "관리자 포인트 지급에 실패하였습니다.");
        }
        return new AjaxResult(true);
    }

    @Transactional
    @Override
    public AjaxResult exchange(PaymentDto.Add add, String bankPassword) {
        if (add.getAmount() == 0) {
            return new AjaxResult(false, "지급할 포인트가 없습니다.");
        }

        Member member = memberService.getMember(WebUtils.userid());
        if (member.getPoint() < add.getAmount()) {
            return new AjaxResult(false, "최대 지급가능 포인트는 " + member.getPoint() + " 입니다.");
        }

        if (!bankPassword.equals(member.getBankPassword())) {
            return new AjaxResult(false, "출금 비밀번호가 일치하지 않습니다.");
        }

        try {
            String send = "";
            String receive = "";
            switch (member.getRole()) {
                case AGENCY4:
                    send = "부본사";
                    receive = "총본사";
                    break;
                case AGENCY3:
                    send = "총판";
                    receive = "부본사";
                    break;
                case AGENCY2:
                    send = "매장";
                    receive = "총판";
                    break;
                case AGENCY1:
                    send = "회원";
                    receive = "매장";
                    break;
            }

            addPoint(PointCode.CHANGE, 0L, member.getUserid(), -add.getAmount(), "포인트 보냄 - " + add.getUserid() + send);
            addPoint(PointCode.CHANGE, 0L, add.getUserid(), add.getAmount(), "포인트 받음 - " + member.getUserid() + receive);
        } catch (RuntimeException e) {
            log.error("총판 포인트 지급에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "포인트 지급에 실패하였습니다.");
        }
        return new AjaxResult(true, add.getAmount() + " 포인트 지급을 완료하였습니다.");
    }

    /**
     * 최종 머니를 리턴한다.
     */
    @Transactional
    @Override
    public long addMoney(MoneyCode moneyCode, Long actionId, String userid, long amount, String memo) {
        Assert.notNull(moneyCode, "PaymentService.addMoney - moneyCode must be not null");
        Assert.notNull(userid, "PaymentService.addMoney - userid must be not null");

        QAddMoney q = QAddMoney.addMoney;
        long count = addMoneyRepository.count(q.userid.eq(userid).and(q.moneyCode.eq(moneyCode)).and(q.actionId.eq(actionId)).and(q.amount.eq(amount)));
        if (count == 0) {
            AddMoney addMoney = new AddMoney(userid, moneyCode, actionId, amount, memo);
            addMoneyRepository.saveAndFlush(addMoney);
        }
        return amount;
    }

    @Transactional
    @Override
    public long rollbackMoney(MoneyCode moneyCode, Long actionId, String userid) {
        Assert.notNull(moneyCode, "PaymentService.rollbackMoney - moneyCode must be not null");
        Assert.notNull(actionId, "PaymentService.rollbackMoney - actionId must be not null");
        Assert.notNull(userid, "PaymentService.rollbackMoney - userid must be not null");

        Money money = moneyRepository.findOne(qm.moneyCode.eq(moneyCode).and(qm.actionId.eq(actionId)).and(qm.userid.eq(userid)).and(qm.cancel.isFalse()));
        if (money == null) return 0;

        long amount = -money.getAmount();
        MoneyCode rollbackCode = MoneyCode.rollback(moneyCode);

        Member member = memberService.getMember(userid);
        if (member == null || member.getRole().getGroup() == Role.Group.ADMIN) {
            throw new PaymentException(userid + "회원을 찾을 수 없습니다.");
        }

        // 롤백대상을 cancel 처리 해 준다.
        money.setCancel(true);
        moneyRepository.saveAndFlush(money);

        return addMoney(rollbackCode, actionId, userid, amount, money.getMemo());
    }

    /**
     * 최종 포인트를 리턴한다.
     */
    @Transactional
    @Override
    public long addPoint(PointCode pointCode, Long actionId, String userid, long amount, String memo) {
        Assert.notNull(pointCode, "PaymentService.addPoint - pointCode must be not null");
        Assert.notNull(userid, "PaymentService.addPoint - userid must be not null");

        QAddPoint q = QAddPoint.addPoint;
        long count = addPointRepository.count(q.userid.eq(userid).and(q.pointCode.eq(pointCode)).and(q.actionId.eq(actionId)).and(q.amount.eq(amount)));

        if (count == 0) {
            AddPoint addPoint = new AddPoint(userid, pointCode, actionId, amount, memo);
            addPointRepository.saveAndFlush(addPoint);
        }

        return amount;
    }

    @Transactional
    @Override
    public long rollbackPoint(PointCode pointCode, Long actionId, String userid) {
        Assert.notNull(pointCode, "PaymentService.rollbackPoint - pointCode must be not null");
        Assert.notNull(actionId, "PaymentService.rollbackPoint - actionId must be not null");
        Assert.notNull(userid, "PaymentService.rollbackPoint - userid must be not null");

        Point point = pointRepository.findOne(qp.pointCode.eq(pointCode).and(qp.userid.eq(userid)).and(qp.actionId.eq(actionId)).and(qp.cancel.isFalse()));
        if (point == null) return 0;

        long amount = -point.getAmount();
        PointCode rollbackCode = PointCode.rollback(pointCode);

        Member member = memberService.getMember(userid);
        if (member == null || member.getRole().getGroup() == Role.Group.ADMIN) {
            throw new PaymentException(point.getUserid() + " 회원을 찾을 수 없습니다.");
        }

        // 롤백 대상을 cancel 처리 해 준다.
        point.setCancel(true);
        pointRepository.saveAndFlush(point);

        return addPoint(rollbackCode, actionId, userid, amount, point.getMemo());

    }

    @Transactional
    @Override
    public long rollbackRecomm(PointCode pointCode, Long actionId, String userid) {
        Assert.notNull(pointCode, "PaymentService.rollbackRecomm - pointCode must be not null");
        Assert.notNull(actionId, "PaymentService.rollbackRecomm - actionId must be not null");
        Assert.notNull(userid, "PaymentService.rollbackRecomm - userid must be not null");

        User recomm = memberService.getRecomm(userid);
        if (recomm == null) return 0;

        Point point = pointRepository.findOne(qp.pointCode.eq(pointCode).and(qp.userid.eq(recomm.getUserid())).and(qp.actionId.eq(actionId)).and(qp.cancel.isFalse()));
        if (point == null) return 0;

        long amount = -point.getAmount();
        PointCode rollbackCode = PointCode.rollback(pointCode);

        Member member = memberService.getMember(recomm.getUserid());
        if (member == null || member.getRole().getGroup() == Role.Group.ADMIN) {
            throw new PaymentException(point.getUserid() + " 회원을 찾을 수 없습니다.");
        }

        // 롤백 대상을 cancel 처리 해 준다.
        point.setCancel(true);
        pointRepository.saveAndFlush(point);

        return addPoint(rollbackCode, actionId, member.getUserid(), amount, point.getMemo());

    }

}
