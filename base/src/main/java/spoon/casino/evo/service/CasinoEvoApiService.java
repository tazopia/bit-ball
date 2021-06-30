package spoon.casino.evo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spoon.casino.evo.domain.CasinoEvoCmd;
import spoon.casino.evo.domain.CasinoEvoResult;
import spoon.casino.evo.entity.CasinoEvoUser;
import spoon.casino.evo.entity.QCasinoEvoUser;
import spoon.casino.evo.repository.CasinoEvoUseridRepository;
import spoon.common.net.HttpParsing;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
import spoon.gameZone.ZoneConfig;
import spoon.member.entity.Member;
import spoon.member.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CasinoEvoApiService {

    private final MemberService memberService;

    private final CasinoEvoPaymentService casinoEvoPaymentService;

    private final CasinoEvoUseridRepository casinoEvoUseridRepository;

    protected static final Map<String, Long> MONEY = new HashMap<>();

    private static final QCasinoEvoUser q = QCasinoEvoUser.casinoEvoUser;

    public CasinoEvoUser getCasinoUser(String userid) {
        CasinoEvoUser evoUser = casinoEvoUseridRepository.findOne(userid);
        if (evoUser == null) {
            evoUser = createCasinoUser(userid);
        }

        return evoUser;
    }

    @Transactional
    public CasinoEvoUser createCasinoUser(String userid) {
        Member member = memberService.getMember(userid);
        String json = HttpParsing.postJson(createAccountUrl(member.getCasinoEvoId(), member.getNickname()));
        if (json == null) return null;

        CasinoEvoResult result = JsonUtils.toModel(json, CasinoEvoResult.class);
        if (result == null) return null;

        if (result.getReturnCode() != 0) {
            log.error("EVO CASINO 아이디 생성 실패 : {}, {}", member.getUserid(), result);
            return null;
        }

        CasinoEvoUser evoUser = CasinoEvoUser.create(member);
        return casinoEvoUseridRepository.save(evoUser);
    }


    public String getGameUrl(String casinoId) {
        String json = HttpParsing.postJson(generateSessionUrl(casinoId));
        if (json == null) return "";

        CasinoEvoResult result = JsonUtils.toModel(json, CasinoEvoResult.class);
        if (result == null) return "";

        if (result.getReturnCode() != 0) return "";

        json = HttpParsing.postJson(getGamePageUrl(result.getSession()));
        if (json == null) return "";

        result = JsonUtils.toModel(json, CasinoEvoResult.class);
        if (result == null) return "";

        if (result.getReturnCode() != 0) return "";

        return result.getGameUrl();
    }

    /**
     * returnCode 1-성공, 기타실패
     * money 회원머니정보
     */
    public CasinoEvoResult balance(CasinoEvoCmd.Balance balance) {
        CasinoEvoUser user = casinoEvoUseridRepository.findOne(q.casinoId.eq(balance.getUserid()));
        Member member = memberService.getMember(user.getUserid());

        if (member == null) return CasinoEvoResult.error("회원을 찾을 수 없습니다.");
        String userid = member.getUserid();

        long money = member.getMoney();
        if (MONEY.containsKey(userid)) {
            if (money != MONEY.get(userid)) money = MONEY.get(userid);
            MONEY.remove(userid);
        }

        return CasinoEvoResult.success(money);
    }

    /**
     * returnCode 1-성공, 기타실패
     * message 사유
     */
    @Transactional
    public CasinoEvoResult change(CasinoEvoCmd.Change change) {
        switch (change.getType()) {
            case "BET": // 머니 마이너스
                return casinoEvoPaymentService.casinoBet(change);
            case "WIN": // 머니 플러스
                return casinoEvoPaymentService.casinoWin(change);
            case "REFUND": // 머니 플러스
                return casinoEvoPaymentService.casinoRefund(change);
        }

        log.error("카지노 change 확인할 수 없는 타입입니다. - {}", change.getType());
        return CasinoEvoResult.error("확인할 수 없는 타입입니다. - " + change.getType());
    }

    @Transactional
    public CasinoEvoResult cancel(CasinoEvoCmd.Cancel cancel) {
        return casinoEvoPaymentService.casinoCancel(cancel);
    }

    private String createAccountUrl(String casinoId, String nickname) {
        String url = String.format(ZoneConfig.getCasinoEvo().getCreateUser(), ZoneConfig.getCasinoEvo().getApiUrl());
        String param = "operatorID=" + ZoneConfig.getCasinoEvo().getApiId()
                + "&userID=" + casinoId
                + "&userAlias=" + nickname;
        return url + "?" + param + "&hash=" + StringUtils.md5(ZoneConfig.getCasinoEvo().getApiKey() + param);
    }

    private String generateSessionUrl(String casinoId) {
        String url = String.format(ZoneConfig.getCasinoEvo().getGenerateSession(), ZoneConfig.getCasinoEvo().getApiUrl());
        String param = "operatorID=" + ZoneConfig.getCasinoEvo().getApiId()
                + "&userID=" + casinoId;
        return url + "?" + param + "&hash=" + StringUtils.md5(ZoneConfig.getCasinoEvo().getApiKey() + param);
    }

    private String getGamePageUrl(String session) {
        String url = String.format(ZoneConfig.getCasinoEvo().getGetGameUrl(), ZoneConfig.getCasinoEvo().getApiUrl());
        String param = "operatorID=" + ZoneConfig.getCasinoEvo().getApiId()
                + "&session=" + session;
        return url + "?" + param + "&hash=" + StringUtils.md5(ZoneConfig.getCasinoEvo().getApiKey() + param);
    }

    private String getGameDetailUrl(String transaction) {
        String url = String.format(ZoneConfig.getCasinoEvo().getGetGameDetail(), ZoneConfig.getCasinoEvo().getApiUrl());
        String param = "transaction=" + transaction;
        return url + "?" + param + "&hash=" + StringUtils.md5(ZoneConfig.getCasinoEvo().getApiKey() + param);
    }
}
