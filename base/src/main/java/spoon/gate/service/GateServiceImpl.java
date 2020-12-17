package spoon.gate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.common.net.HttpParsing;
import spoon.common.utils.ErrorUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.common.utils.WebUtils;
import spoon.config.entity.JsonConfig;
import spoon.config.service.ConfigService;
import spoon.gameZone.ZoneConfig;
import spoon.gate.domain.GateConfig;
import spoon.gate.domain.GateDto;
import spoon.member.domain.CurrentUser;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;
import spoon.payment.domain.MoneyCode;
import spoon.payment.entity.Money;
import spoon.payment.service.PaymentService;
import spoon.support.web.AjaxResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class GateServiceImpl implements GateService {

    private MemberService memberService;

    private PaymentService paymentService;

    private GateMoneyService gateMoneyService;

    private ConfigService JsonConfigService;

    public GateServiceImpl(MemberService memberService, PaymentService paymentService, GateMoneyService gateMoneyService, ConfigService jsonConfigService) {
        this.memberService = memberService;
        this.paymentService = paymentService;
        this.gateMoneyService = gateMoneyService;
        this.JsonConfigService = jsonConfigService;

        JsonConfig gateConfig = JsonConfigService.getOne("gate");
        if (gateConfig == null) {
            ZoneConfig.setGate(new GateConfig());
        } else {
            ZoneConfig.setGate(JsonUtils.toModel(gateConfig.getJson(), GateConfig.class));
        }
    }

    @Transactional
    @Override
    public boolean updateGateConfig(GateConfig gateConfig) {
        try {
            JsonConfigService.updateZoneConfig("gate", JsonUtils.toString(gateConfig));
            ZoneConfig.setGate(gateConfig);
        } catch (RuntimeException e) {
            log.error("파워볼 설정 변경에 실패하였습니다. - {}", e.getMessage());
            log.info("{}", ErrorUtils.trace(e.getStackTrace()));
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public String login() {
        CurrentUser user = WebUtils.user();
        if (user == null) return "회원을 찾을 수 없습니다.";

        String result = HttpParsing.getJson(loginUrl(user));
        if (result == null) return "사이트에 접속할 수 없습니다.";

        switch (result.trim()) {
            case "1":
            case "100":
                return "";
            case "101":
                return "총판이 존재하지 않습니다.\n\n관리자에 문의 바랍니다.";
        }

        return "사이트에 접속 할 수 없습니다.\n\n관리자에 문의 바랍니다.";
    }

    @Override
    public String getLiveUrl() {
        GateConfig c = ZoneConfig.getGate();
        String userid = WebUtils.userid();
        if (userid == null) userid = "";
        String md5 = makeMd5(userid);

        return c.getGateUrl() + String.format("/r_live/game_live.asp?member_site=%s&site_key=%s&gate_id=%s&gate_key=%s", c.getGateUserid(), c.getGateKey(), userid, md5);
    }

    @Override
    public String getSudaUrl() {
        GateConfig c = ZoneConfig.getGate();
        String userid = WebUtils.userid();
        if (userid == null) userid = "";
        String md5 = makeMd5(userid);

        return c.getGateUrl() + String.format("/r_nsgs/game.asp?member_site=%s&site_key=%s&gate_id=%s&gate_key=%s", c.getGateUserid(), c.getGateKey(), userid, md5);
    }

    @Override
    public String getGraphUrl() {
        GateConfig c = ZoneConfig.getGate();
        String userid = WebUtils.userid();
        if (userid == null) userid = "";
        String md5 = makeMd5(userid);

        return c.getGateUrl() + String.format("/r_nsgh/play/index.asp?member_site=%s&site_key=%s&gate_id=%s&gate_key=%s", c.getGateUserid(), c.getGateKey(), userid, md5);
    }

    @Override
    public long getGameMoney() {
        String userid = WebUtils.userid();
        String result = HttpParsing.getJson(moneyUrl(userid));
        if (result == null) return 0;

        return Long.parseLong(result.trim());
    }

    @Transactional
    @Override
    public AjaxResult exchange(long amount, int code) {
        AjaxResult result = new AjaxResult();
        String userid = WebUtils.userid();

        if (userid == null) {
            result.setMessage("머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
            return result;
        }

        Member member = memberService.getMember(userid);

        try {
            if (code == 1) {
                if (member.getMoney() < amount) {
                    result.setMessage("보유머니가 부족합니다.");
                    return result;
                }

                paymentService.addMoney(MoneyCode.GATE_OUT, 0L, member.getUserid(), -amount, "게이트로 머니 보냄");
                String ex = HttpParsing.getJson(exchangeUrl(userid, amount, code));

                if (ex == null) {
                    throw new RuntimeException("GATE_OUT 머니페이지 응답이 없습니다.");
                }

                if ("1".equals(ex)) {
                    result.setSuccess(true);
                    return result;
                }
                throw new RuntimeException("응답코드 : " + ex + " - 머니 보내기에 실패하였습니다.");
            } else if (code == 2) {
                String m = HttpParsing.getJson(moneyUrl(userid));

                if (m == null) {
                    throw new RuntimeException("GATE_IN 머니페이지 응답이 없습니다.");
                }

                long gameMoney = Long.parseLong(m.trim());
                if (gameMoney < amount) {
                    result.setMessage("게임머니가 부족합니다.");
                    return result;
                }

                paymentService.addMoney(MoneyCode.GATE_IN, 0L, member.getUserid(), amount, "게이트에서 머니 받음");

                String ex = HttpParsing.getJson(exchangeUrl(userid, amount, code));

                if (ex == null) {
                    throw new RuntimeException("GATE_OUT 머니페이지 응답이 없습니다.");
                }

                if ("1".equals(ex)) {
                    result.setSuccess(true);
                    return result;
                } else if ("100".equals(ex)) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    result.setMessage("게임머니가 부족합니다.");
                    return result;
                }
                throw new RuntimeException("응답코드 : " + ex + " - 머니 가져오기에 실패하였습니다.");
            }

        } catch (RuntimeException e) {
            log.error("게이트 머니전환 실패 : {}", e.getMessage());
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            result.setMessage("머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
            return result;
        }

        result.setMessage("머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        return result;
    }

    @Override
    public Page<Money> page(GateDto.Command command, Pageable pageable) {
        return gateMoneyService.findAll(command, pageable);
    }

    private String loginUrl(CurrentUser user) {
        GateConfig c = ZoneConfig.getGate();
        String userid = user.getUserid();
        String agency = user.getAgency1();
        if (StringUtils.empty(agency)) {
            agency = c.getGateAgency();
        }
        String md5 = makeMd5(userid);

        return c.getGateUrl() + String.format("/api/do_reg.asp?site_id=%s&site_key=%s&member_id=%s&member_key=%s&member_agent=%s", c.getGateUserid(), c.getGateKey(), userid, md5, agency);
    }

    private String exchangeUrl(String userid, long money, int code) {
        GateConfig c = ZoneConfig.getGate();
        return c.getGateUrl() + String.format("/api/do_trade.asp?site_id=%s&site_key=%s&member_id=%s&trade_type=%d&trade_amount=%d", c.getGateUserid(), c.getGateKey(), userid, code, money);
    }

    private String moneyUrl(String userid) {
        GateConfig c = ZoneConfig.getGate();
        return c.getGateUrl() + String.format("/api/do_balance.asp?site_id=%s&member_id=%s", c.getGateUserid(), userid);
    }

    private String makeMd5(String userid) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(userid.getBytes());
            byte[] b = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aB : b) {
                sb.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
