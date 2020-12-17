package spoon.banking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.DepositDto;
import spoon.banking.entity.Banking;
import spoon.common.utils.DateUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.config.domain.SiteConfig;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.monitor.service.MonitorService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class BankingDepositServiceImpl implements BankingDepositService {

    private MemberService memberService;

    private BankingService bankingService;

    private MonitorService monitorService;

    @Override
    public AjaxResult deposit(DepositDto.Add add) {
        String userid = WebUtils.userid();

        if (userid == null) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        // 현재 진행중인 충/환전 신청이 있는지 확인
        if (bankingService.existWorkBanking(userid)) {
            return new AjaxResult(false, "현재 처리중인 충/환전 신청이 있습니다.");
        }

        Member member = memberService.getMember(userid);

        Banking banking = new Banking();
        banking.setUserid(member.getUserid());
        banking.setNickname(member.getNickname());
        banking.setAgency1(member.getAgency1());
        banking.setAgency2(member.getAgency2());
        banking.setAgency3(member.getAgency3());
        banking.setAgency4(member.getAgency4());
        banking.setRole(member.getRole());
        banking.setLevel(member.getLevel());
        banking.setDepositor(member.getDepositor());
        banking.setAccount(member.getAccount());
        banking.setBank(member.getBank());
        banking.setBankingCode(BankingCode.IN);
        banking.setMoney(member.getMoney());
        banking.setPoint(member.getPoint());
        banking.setAmount(add.getAmount());
        banking.setRegDate(new Date());
        banking.setWorker(member.getUserid());
        banking.setIp(WebUtils.ip());

        int level = member.getLevel();
        long bonusPoint = 0;
        if (member.getDeposit() == 0) { // 가입첫충
            bonusPoint = getBonusPoint(level, "가입첫충");
            banking.setBonus("가입첫충");
        } else if (bankingService.isFirstDeposit(userid)) { // 첫충
            bonusPoint = getBonusPoint(level, "첫충");
            banking.setBonus("첫충");
        } else if (bankingService.isEveryDeposit(userid)) { // 매충
            bonusPoint = getBonusPoint(level, "매충");
            banking.setBonus("매충");
        }
        // 보너스
        banking.setBonusPoint((long) (bonusPoint / 100D * banking.getAmount()));
        // 충전 처리를 완료한다.
        boolean success = bankingService.addDeposit(banking);

        if (success) {
            monitorService.checkDeposit();
            return new AjaxResult(true);
        }
        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

    @Override
    public AjaxResult delete(long id) {
        String userid = WebUtils.userid();

        if (userid == null) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        Banking banking = bankingService.getBanking(id);

        if (!banking.getUserid().equals(userid) || banking.getBankingCode() != BankingCode.IN) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        if (!banking.isClosing()) {
            return new AjaxResult(false, "대기중인 충전신청을 삭제할 수 없습니다.");
        }

        boolean success = bankingService.delete(id);

        if (success) {
            return new AjaxResult(true);
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

    @Override
    public AjaxResult cancel(long id) {
        Role role = WebUtils.role();

        if (role == null) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        Banking banking = bankingService.getBanking(id);

        // 회원일 경우 신청내역의 아이디가 일치하여야 한다.
        if (role == Role.USER && !banking.getUserid().equals(WebUtils.userid())) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        if (banking.getBankingCode() != BankingCode.IN) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        if (banking.isCancel()) {
            return new AjaxResult(false, "이미 취소된 충전신청 입니다.");
        }

        if (banking.isClosing()) {
            return new AjaxResult(false, "이미 충전신청이 완료 되었습니다.");
        }

        boolean success = bankingService.cancelDeposit(id);
        if (success) {
            monitorService.checkDeposit();
            return new AjaxResult(true, role == Role.USER ? "" : "충전신청을 취소하였습니다.");
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

    @Override
    public AjaxResult submit(long id) {
        Banking banking = bankingService.getBanking(id);

        if (banking.getBankingCode() != BankingCode.IN) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        if (banking.isCancel()) {
            return new AjaxResult(false, "이미 취소된 충전신청 입니다.");
        }

        if (banking.isClosing()) {
            return new AjaxResult(false, "이미 충전신청이 완료 되었습니다.");
        }

        boolean success = bankingService.submitDeposit(id);

        if (success) {
            monitorService.checkDeposit();
            return new AjaxResult(true, "충전신청을 완료 하였습니다.");
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }


    @Override
    public AjaxResult stop(long id) {
        bankingService.stop(id);
        monitorService.checkDeposit();
        return new AjaxResult(true);
    }

    @Override
    public AjaxResult rollback(long id) {
        Banking banking = bankingService.getBanking(id);

        if (banking.isCancel()) {
            return new AjaxResult(false, "이미 취소된 충전신청 입니다.");
        }

        if (!banking.isClosing()) { // 클로징이 안 되었다는 것은 입금 처리가 되지 않았다는 것이다.
            return new AjaxResult(false, "포인트를 복원 할 수 없습니다.");
        }

        if (banking.isReset()) {
            return new AjaxResult(false, "이미 충전취소 - 포인트 복원된 충전요청 입니다.");
        }

        return bankingService.rollbackDeposit(id);
    }

    private long getBonusPoint(int level, String bonusType) {
        SiteConfig.Point point = Config.getSiteConfig().getPoint();
        int w = DateUtils.week();
        long bonus;

        //log.error("페이먼트: {}, 이벤트: {}, 보너스: {}, 첫충: {}", point.isEventPayment(), point.getEvent()[w], point.getEventRate()[w], point.isEventFirst());

        if ("가입첫충".equals(bonusType)) { // 가입첫충
            if (point.isEventPayment() && point.getEvent()[w] && point.getEventRate()[w] > 0) {
                bonus = Math.max(point.getEventRate()[w], point.getJoinRate()[level]);
            } else {
                bonus = point.getJoinRate()[level];
            }
        } else if ("첫충".equals(bonusType)) { // 첫충
            if (point.isEventPayment() && point.getEvent()[w] && point.getEventRate()[w] > 0) {
                bonus = Math.max(point.getEventRate()[w], point.getFirstRate()[level]);
            } else {
                bonus = point.getFirstRate()[level];
            }
        } else { // 매충 (첫충이 아닐때만)
            if (point.isEventPayment() && !point.isEventFirst() && point.getEvent()[w] && point.getEventRate()[w] > 0) {
                bonus = Math.max(point.getEventRate()[w], point.getEveryRate()[level]);
            } else {
                bonus = point.getEveryRate()[level];
            }
        }
        return bonus;
    }
}
