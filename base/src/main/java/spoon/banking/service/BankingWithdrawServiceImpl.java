package spoon.banking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.Rolling;
import spoon.banking.domain.WithdrawDto;
import spoon.banking.entity.Banking;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.domain.Config;
import spoon.mapper.BankingMapper;
import spoon.member.domain.Role;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.monitor.service.MonitorService;
import spoon.support.web.AjaxResult;

import java.util.Date;

@AllArgsConstructor
@Service
public class BankingWithdrawServiceImpl implements BankingWithdrawService {

    private BankingService bankingService;

    private MemberService memberService;

    private BankingMapper bankingMapper;

    private MonitorService monitorService;

    @Override
    public AjaxResult withdraw(WithdrawDto.Add add) {
        String userid = WebUtils.userid();

        if (userid == null) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        // 3시간 이내로 환전 신청 내역이 있는지 확인
        if (bankingService.existBeforeMinutesWithdraw(userid, Config.getSiteConfig().getPoint().getWithdrawTerm())) {
            return new AjaxResult(false, "환전 후 " + Config.getSiteConfig().getPoint().getWithdrawTerm() + "분이 경과하셔야 신청이 가능 합니다.");
        }

        Member member = memberService.getMember(userid);

        // 환전 비밀번호 오류
        if (!add.getBankPassword().equals(member.getBankPassword())) {
            return new AjaxResult(false, "환전 비밀번호가 일치하지 않습니다.");
        }

        // 잔고 부족
        if (add.getAmount() > member.getMoney()) {
            return new AjaxResult(false, "현재 환전 가능한 최대 금액은 " + String.format("%,d", member.getMoney()) + "원 입니다.");
        }

        // 최소 환전 금액
        long withdrawMin = Config.getSiteConfig().getPoint().getWithdrawMin();
        if (add.getAmount() < withdrawMin) {
            return new AjaxResult(false, "최소 환전 금액은 " + String.format("%,d", withdrawMin) + "원 입니다.");
        }

        if (bankingService.existWorkBanking(userid)) {
            return new AjaxResult(false, "현재 처리중인 충/환전 신청 건수가 있습니다.");
        }

        if (!Config.getSiteConfig().getPoint().isCanWithdraw() && member.getDeposit() == 0 && member.getRole() == Role.USER) {
            return new AjaxResult(false, "입금이 없는 환전신청을 하실 수 없습니다.");
        }

        Rolling rolling = getRolling(userid);

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
        banking.setMoney(member.getMoney());
        banking.setPoint(member.getPoint());
        banking.setBankingCode(BankingCode.OUT);
        banking.setAmount(add.getAmount());
        banking.setRegDate(new Date());
        banking.setWorker(member.getUserid());
        banking.setIp(WebUtils.ip());
        banking.setRolling(JsonUtils.toString(rolling));

        // 환전 처리를 완료한다.
        boolean success = bankingService.addWithdraw(banking);

        if (success) {
            monitorService.checkWithdraw();
            return new AjaxResult(true);
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

    @Override
    public AjaxResult sellerWithdraw(WithdrawDto.Add add) {
        String userid = WebUtils.userid();

        if (userid == null) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        Member member = memberService.getMember(userid);

        // 환전 비밀번호 오류
        if (!add.getBankPassword().equals(member.getBankPassword())) {
            return new AjaxResult(false, "환전 비밀번호가 일치하지 않습니다.");
        }

        // 잔고 부족
        if (add.getAmount() > member.getPoint()) {
            return new AjaxResult(false, "현재 환전 가능한 최대 금액은 " + String.format("%,d", member.getMoney()) + "원 입니다.");
        }

        // 최소 환전 금액
        long withdrawMin = Config.getSiteConfig().getPoint().getWithdrawMin();
        if (add.getAmount() < withdrawMin) {
            return new AjaxResult(false, "최소 환전 금액은 " + String.format("%,d", withdrawMin) + "원 입니다.");
        }

        if (bankingService.existWorkBanking(userid)) {
            return new AjaxResult(false, "현재 처리중인 충/환전 신청 건수가 있습니다.");
        }

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
        banking.setMoney(member.getMoney());
        banking.setPoint(member.getPoint());
        banking.setBankingCode(BankingCode.OUT);
        banking.setAmount(add.getAmount());
        banking.setRegDate(new Date());
        banking.setWorker(member.getUserid());
        banking.setIp(WebUtils.ip());
        banking.setRolling("");

        // 환전 처리를 완료한다.
        boolean success = bankingService.addSellerWithdraw(banking);

        if (success) {
            monitorService.checkWithdraw();
            return new AjaxResult(true, "출금 신청을 완료 하였습니다.");
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

    @Override
    public Rolling getRolling(String userid) {
        Rolling rolling = bankingMapper.getRolling(userid);
        return rolling == null ? new Rolling() : rolling;
    }

    @Override
    public AjaxResult submit(long id, long fees) {
        Banking banking = bankingService.getBanking(id);

        if (banking.getBankingCode() != BankingCode.OUT) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        if (banking.isCancel()) {
            return new AjaxResult(false, "이미 취소된 환전신청 입니다.");
        }

        if (banking.isClosing()) {
            return new AjaxResult(false, "이미 환전신청이 완료 되었습니다.");
        }

        banking.setFees(fees);
        boolean success = bankingService.submitWithdraw(banking);

        if (success) {
            monitorService.checkWithdraw();
            return new AjaxResult(true, "환전신청을 완료 하였습니다.");
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

    @Override
    public AjaxResult rollback(long id) {
        Banking banking = bankingService.getBanking(id);

        if (banking.isCancel()) {
            return new AjaxResult(false, "이미 취소된 환전신청 입니다.");
        }

        if (banking.isReset()) {
            return new AjaxResult(false, "이미 취소된 환전신청 입니다.");
        }

        AjaxResult result = bankingService.rollbackWithdraw(id);
        if (result.isSuccess()) {
            monitorService.checkWithdraw();
        }

        return result;
    }

    @Override
    public AjaxResult stop(long id) {
        bankingService.stop(id);
        monitorService.checkWithdraw();
        return new AjaxResult(true);
    }

    @Override
    public AjaxResult delete(long id) {
        String userid = WebUtils.userid();

        if (userid == null) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        Banking banking = bankingService.getBanking(id);

        if (!banking.getUserid().equals(userid) || banking.getBankingCode() != BankingCode.OUT) {
            return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
        }

        if (!banking.isClosing()) {
            return new AjaxResult(false, "대기중인 환전신청을 삭제할 수 없습니다.");
        }

        boolean success = bankingService.delete(id);

        if (success) {
            return new AjaxResult(true);
        }

        return new AjaxResult(false, "현재 요청을 수행 할 수 없습니다.");
    }

}
