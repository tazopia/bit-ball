package spoon.sun.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.WebUtils;
import spoon.config.entity.JsonConfig;
import spoon.config.service.ConfigService;
import spoon.gameZone.ZoneConfig;
import spoon.member.domain.CurrentUser;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.entity.Money;
import spoon.payment.entity.QMoney;
import spoon.payment.repository.MoneyRepository;
import spoon.payment.service.PaymentService;
import spoon.sun.domain.*;
import spoon.sun.entity.QSunId;
import spoon.sun.entity.SunId;
import spoon.sun.repository.SunIdRepository;
import spoon.support.web.AjaxResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SunService {

    private final SunIdRepository sunIdRepository;

    private final ConfigService jsonConfigService;

    private final MemberService memberService;

    private final PaymentService paymentService;

    private final MoneyRepository moneyRepository;

    private static final QSunId q = QSunId.sunId;

    @PostConstruct
    public void init() {
        JsonConfig sunConfig = jsonConfigService.getOne("sun");
        if (sunConfig == null) {
            ZoneConfig.setSun(new SunConfig());
        } else {
            ZoneConfig.setSun(JsonUtils.toModel(sunConfig.getJson(), SunConfig.class));
        }
    }

    public String getGnum() {
        CurrentUser user = WebUtils.user();
        if (user == null) return "";

        SunId sunId = sunIdRepository.findOne(q.userid.eq(user.getUserid()));

        if (sunId != null) {
            return sunId.getGnum();
        }

        // 아이디를 만든다.
        return createUser(user);
    }

    @Transactional
    String createUser(CurrentUser user) {
        SunConfig c = ZoneConfig.getSun();
        String json = HttpParsing.getJson(String.format(c.getCreateUser(), c.getMasterId(), c.getMasterPassword(), user.getSunid()));
        if (json == null) return "";

        List<SunResponseDto.CreateUser> createUserList = JsonUtils.toSunUserList(json);
        if (createUserList.size() == 0) return "";

        SunResponseDto.CreateUser createUser = createUserList.get(0);

        if (!createUser.getRespCode().equals("000")) {
            // 에러가 발생
            log.error("{}, 회원생성 실패", createUser.getRespCode());
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return "";
        }

        sunIdRepository.save(SunId.of(user.getUserid(), createUser.getGNum()));
        return createUser.getGNum();
    }


    public String getSlotUrl(String gnum) {
        return String.format(ZoneConfig.getSun().getSunSlotUrl(), gnum);
    }

    public String getLiveUrl(String gnum) {
        return String.format(ZoneConfig.getSun().getSunCasinoUrl(), gnum);
    }

    public List<SunBalance> getBalanceAll() {
        CurrentUser user = WebUtils.user();
        if (user == null) return Collections.emptyList();

        List<SunBalance> list = new ArrayList<>();
        for (SunGame game : SunGame.values()) {
            SunBalance balance = getBalance(user.getSunid(), game);
            if (balance != null) {
                list.add(balance);
            }
        }
        return list;
    }

    public SunBalance getBalance(String gnum, SunGame game) {
        SunConfig c = ZoneConfig.getSun();
        String json = HttpParsing.getJson(String.format(c.getBalanceUser(), c.getMasterId(), c.getMasterPassword(), gnum, game.getGameNo()));
        if (json == null) return null;

        List<SunResponseDto.Balance> balanceList = JsonUtils.toSunBalanceList(json);
        if (balanceList.size() == 0) return null;

        return SunBalance.of(game, balanceList.get(0));
    }

    @Transactional
    public AjaxResult sendMoney(SunDto.SendMoney send) {
        CurrentUser user = WebUtils.user();

        if (user == null) {
            return new AjaxResult(false, "머니보내기를 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }

        Member member = memberService.getMember(user.getUserid());
        SunConfig c = ZoneConfig.getSun();

        if (member.getMoney() < send.getMoney()) {
            return new AjaxResult(false, "보유머니가 부족합니다.");
        }

        try {
            paymentService.addMoney(MoneyCode.SUN_OUT, 0L, member.getUserid(), -send.getMoney(), "선카지노 (" + SunGame.numOf(send.getGameNo()).getValue() + ") 머니 보냄");
            String json = HttpParsing.getJson(String.format(c.getSendMoneyUrl(), c.getMasterId(), c.getMasterPassword(), user.getSunid(), send.getGameNo(), send.getMoney()));

            if (json == null) {
                throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
            }

            List<SunResponseDto.Credit> creditList = JsonUtils.toSunCreditList(json);

            if (creditList.size() == 0) {
                throw new RuntimeException("응답이 잘못 되었습니다. 잠시후 다시 이용하세요.");
            }

            SunResponseDto.Credit credit = creditList.get(0);

            if (!"000".equals(credit.getRespCode())) {
                String msg = createMoneyMessage(credit.getRespCode());
                throw new RuntimeException(msg);
            }

        } catch (RuntimeException e) {
            log.error("머니 보내기에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "머니보내기를 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }

        return new AjaxResult(true, SunGame.numOf(send.getGameNo()).getValue() + "에 " + send.getMoney() + "원을 보냈습니다.");
    }

    @Transactional
    public AjaxResult receiveMoney(SunDto.ReceiveMoney receive) {
        CurrentUser user = WebUtils.user();

        if (user == null) {
            return new AjaxResult(false, "머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }

        SunConfig c = ZoneConfig.getSun();

        try {
            String json = HttpParsing.getJson(String.format(c.getReceiveMoneyUrl(), c.getMasterId(), c.getMasterPassword(), user.getSunid(), receive.getGameNo(), receive.getMoney()));
            if (json == null) {
                throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
            }

            List<SunResponseDto.Debit> debitList = JsonUtils.toSunDebit(json);
            if (debitList.size() == 0) {
                throw new RuntimeException("응답이 잘못 되었습니다. 잠시후 다시 이용하세요.");
            }

            SunResponseDto.Debit debit = debitList.get(0);

            paymentService.addMoney(MoneyCode.SUN_IN, 0L, user.getUserid(), debit.getAmount(), "선카지노 (" + SunGame.numOf(receive.getGameNo()).getValue() + ") 머니 받음");

            if (!"000".equals(debit.getRespCode())) {
                String msg = createMoneyMessage(debit.getRespCode());
                throw new RuntimeException(msg);
            }

        } catch (RuntimeException e) {
            log.error("머니 받기에 에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }
        return new AjaxResult(true, SunGame.numOf(receive.getGameNo()).getValue() + "에서 " + receive.getMoney() + "원을 받았습니다.");
    }

    private String createMoneyMessage(String respCode) {
        switch (respCode) {
            case "001":
                return "001: 총판 ID가 일치하지 않습니다.";
            case "002":
                return "002: ID 가 일치하지 않습니다.";
            case "003":
                return "003: 머니 전환 및 이동에 실패하였습니다.";
            case "004":
                return "004: 현재 게임서버 점검중입니다.";
            case "005":
                return "005: 잘못된 접근입니다.";
            default:
                return respCode + ":머니 전환 및 이동에 실패하였습니다.";
        }
    }

    @Transactional
    public boolean updateConfig(SunConfig sunConfig) {
        SunConfig org = ZoneConfig.getSun();
        org.setMasterId(sunConfig.getMasterId());
        org.setMasterPassword(sunConfig.getMasterPassword());

        try {
            jsonConfigService.updateZoneConfig("sun", JsonUtils.toString(org));
            ZoneConfig.setSun(org);
        } catch (RuntimeException e) {
            log.error("선카지노 환경설정 업데이트에 실패하였습니다.", e);
            return false;
        }
        return true;
    }

    public Page<Money> page(SunDto.Command command, Pageable pageable) {
        QMoney q = QMoney.money;

        BooleanBuilder builder = new BooleanBuilder(q.moneyCode.in(MoneyCode.SUN_IN, MoneyCode.SUN_OUT));
        builder.and(q.regDate.goe(DateUtils.start(command.getSdate()))).and(q.regDate.lt(DateUtils.end(command.getEdate())));

        return moneyRepository.findAll(builder, pageable);

    }
}
