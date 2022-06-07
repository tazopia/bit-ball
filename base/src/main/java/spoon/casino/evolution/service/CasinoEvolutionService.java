package spoon.casino.evolution.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import spoon.casino.evolution.domain.CasinoEvolutionConfig;
import spoon.casino.evolution.domain.CasinoEvolutionDto;
import spoon.casino.evolution.domain.CasinoEvolutionGame;
import spoon.casino.evolution.domain.CasinoEvolutionResult;
import spoon.casino.evolution.entity.CasinoEvolutionBet;
import spoon.casino.evolution.entity.CasinoEvolutionId;
import spoon.casino.evolution.entity.QCasinoEvolutionBet;
import spoon.casino.evolution.entity.QCasinoEvolutionId;
import spoon.casino.evolution.repository.CasinoEvolutionBetRepository;
import spoon.casino.evolution.repository.CasinoEvolutionIdRepository;
import spoon.common.net.HttpParsing;
import spoon.common.utils.DateUtils;
import spoon.common.utils.JsonUtils;
import spoon.common.utils.StringUtils;
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
import spoon.support.web.AjaxResult;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CasinoEvolutionService {

    private final CasinoEvolutionIdRepository casinoEvolutionIdRepository;

    private final CasinoEvolutionBetRepository casinoEvolutionBetRepository;

    private final ConfigService jsonConfigService;

    private final MemberService memberService;

    private final PaymentService paymentService;

    private final MoneyRepository moneyRepository;

    private static final QCasinoEvolutionId q = QCasinoEvolutionId.casinoEvolutionId;

    private static final List<CasinoEvolutionGame> gameList = new ArrayList<>();

    private static final Map<String, String> headers = new HashMap<>();

    private static final Map<String, String> patch = new HashMap<>();

    @PostConstruct
    public void init() {
        JsonConfig config = jsonConfigService.getOne("evolution");
        if (config == null) {
            ZoneConfig.setCasinoEvolution(new CasinoEvolutionConfig());
        } else {
            ZoneConfig.setCasinoEvolution(JsonUtils.toModel(config.getJson(), CasinoEvolutionConfig.class));
        }

        headers.put("Authorization", "Bearer " + ZoneConfig.getCasinoEvolution().getApiKey());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");

        patch.put("Authorization", "Bearer " + ZoneConfig.getCasinoEvolution().getApiKey());
        patch.put("Accept", "application/json");
        patch.put("Content-Type", "application/json");
        patch.put("X-HTTP-Method-Override", "PATCH");

        this.getGames().stream().filter(x -> "slot".equals(x.getType())).forEach(gameList::add);
    }

    public String getCasinoId() {
        CurrentUser user = WebUtils.user();
        if (user == null) return "";

        CasinoEvolutionId casinoId = casinoEvolutionIdRepository.findOne(q.userid.eq(user.getUserid()));

        if (casinoId != null) {
            return casinoId.getCasinoid();
        }

        // 아이디를 만든다.
        return createUser(user);
    }


    @Transactional
    public String createUser(CurrentUser user) {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.postCasinoEvolution(String.format(c.getCreateUser(), user.getCasinoEvolutionId(), user.getNickname()), headers);
        if (json == null) return "";

        CasinoEvolutionResult.createUser createUser = JsonUtils.toModel(json, CasinoEvolutionResult.createUser.class);
        if (createUser == null) return "";

        if (StringUtils.empty(createUser.getUsername())) {
            // 에러가 발생
            log.error("{}, 회원생성 실패", createUser.getMessage());
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return "";
        }

        casinoEvolutionIdRepository.save(CasinoEvolutionId.of(user.getUserid(), createUser.getUsername()));
        return createUser.getUsername();
    }

    public CasinoEvolutionResult.User getUser(String username) {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.getCasinoEvolution(String.format(c.getUser(), username), headers);
        if (json == null) return null;
        return JsonUtils.toModel(json, CasinoEvolutionResult.User.class);
    }

    public CasinoEvolutionResult.Token getToken(String username) {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.patchCasinoEvolution(String.format(c.getToken(), username), patch);
        if (json == null) return null;
        return JsonUtils.toModel(json, CasinoEvolutionResult.Token.class);
    }

    public AjaxResult getGameUrl(CasinoEvolutionDto.Game param) {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.getCasinoEvolution(String.format(c.getGameUrl(), param.getId(), param.getToken()), headers);
        if (json == null) return new AjaxResult(false, "현재 게임에 접속할 수 없습니다.");

        CasinoEvolutionResult.Game game = JsonUtils.toModel(json, CasinoEvolutionResult.Game.class);
        if (game == null) return new AjaxResult(false, "현재 게임에 접속할 수 없습니다.");

        AjaxResult result = new AjaxResult(true, "");
        result.setUrl(game.getLink());

        return result;
    }

    public List<CasinoEvolutionGame> getGames() {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.getCasinoEvolution(c.getGameList(), headers);

        if (json == null) return Collections.emptyList();

        return JsonUtils.toCasinoGameList(json);
    }

    public List<CasinoEvolutionGame> getSlot() {
        return gameList;
    }

    public List<CasinoEvolutionGame> getLobby() {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.getCasinoEvolution(c.getLobbyList(), headers);

        if (json == null) return Collections.emptyList();

        return JsonUtils.toCasinoGameList(json);
    }

    @Transactional
    public AjaxResult sendMoney(CasinoEvolutionDto.SendMoney send) {
        CurrentUser user = WebUtils.user();

        if (user == null) {
            return new AjaxResult(false, "머니보내기를 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }

        Member member = memberService.getMember(user.getUserid());
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();

        if (member.getMoney() < send.getMoney()) {
            return new AjaxResult(false, "보유머니가 부족합니다.");
        }

        try {
            paymentService.addMoney(MoneyCode.EVO_OUT, 0L, member.getUserid(), -send.getMoney(), "카지노 머니 보냄");
            String json = HttpParsing.postCasinoEvolution(String.format(c.getAddBalance(), send.getCasinoId(), send.getMoney()), headers);

            if (json == null) {
                throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
            }

            CasinoEvolutionResult.Balance result = JsonUtils.toModel(json, CasinoEvolutionResult.Balance.class);

            if (result == null) {
                throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
            }

            if (StringUtils.notEmpty(result.getMessage())) {
                log.error("머니 보내기 실패 : {}", result.getMessage());
                throw new RuntimeException(result.getMessage());
            }

        } catch (RuntimeException e) {
            log.error("머니 보내기에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "머니보내기를 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }

        return new AjaxResult(true, "카지노에 " + send.getMoney() + "원을 보냈습니다.");
    }

    @Transactional
    public AjaxResult receiveMoney(CasinoEvolutionDto.ReceiveMoney receive) {
        CurrentUser user = WebUtils.user();

        if (user == null) {
            return new AjaxResult(false, "머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }

        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String message;

        try {
            String json = HttpParsing.postCasinoEvolution(String.format(c.getSubBalance(), receive.getCasinoId()), headers);
            if (json == null) {
                throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
            }

            CasinoEvolutionResult.Balance result = JsonUtils.toModel(json, CasinoEvolutionResult.Balance.class);

            if (result == null) {
                throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
            }

            if (StringUtils.notEmpty(result.getMessage())) {
                throw new RuntimeException(result.getMessage());
            }

            paymentService.addMoney(MoneyCode.EVO_IN, 0L, user.getUserid(), -result.getAmount(), "카지노 머니 받음");
            message = "카지노에서 " + -result.getAmount() + "원을 받았습니다.";

        } catch (RuntimeException e) {
            log.error("머니 받기에 에 실패하였습니다.", e);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return new AjaxResult(false, "머니전환을 할 수 없습니다.\n\n잠시후 다시 이용하시기 바랍니다.");
        }
        return new AjaxResult(true, message);
    }

    public CasinoEvolutionResult.Exchange exchangeMoney(CasinoEvolutionDto.ReceiveMoney receive) {
        CasinoEvolutionConfig c = ZoneConfig.getCasinoEvolution();
        String json = HttpParsing.postCasinoEvolution(String.format(c.getExchange(), receive.getCasinoId()), headers);
        if (json == null) {
            throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
        }

        CasinoEvolutionResult.Exchange result = JsonUtils.toModel(json, CasinoEvolutionResult.Exchange.class);

        if (result == null) {
            throw new RuntimeException("응답이 없습니다. 잠시후 다시 이용하세요.");
        }

        return result;
    }

    public Page<Money> page(CasinoEvolutionDto.Exchange command, Pageable pageable) {
        QMoney q = QMoney.money;

        BooleanBuilder builder = new BooleanBuilder(q.moneyCode.in(MoneyCode.EVO_IN, MoneyCode.EVO_OUT));
        if (StringUtils.notEmpty(command.getUsername())) {
            builder.and(q.userid.contains(command.getUsername()).or(q.nickname.contains(command.getUsername())));
        }

        builder.and(q.regDate.goe(DateUtils.start(command.getSdate()))).and(q.regDate.lt(DateUtils.end(command.getEdate())));

        return moneyRepository.findAll(builder, pageable);

    }

    @Transactional
    public boolean updateConfig(CasinoEvolutionConfig config) {
        CasinoEvolutionConfig org = ZoneConfig.getCasinoEvolution();
        org.setApiKey(config.getApiKey());
        org.setApiUrl(config.getApiUrl());

        try {
            jsonConfigService.updateZoneConfig("evolution", JsonUtils.toString(org));
            ZoneConfig.setCasinoEvolution(org);

            headers.put("Authorization", "Bearer " + ZoneConfig.getCasinoEvolution().getApiKey());
            patch.put("Authorization", "Bearer " + ZoneConfig.getCasinoEvolution().getApiKey());
        } catch (RuntimeException e) {
            log.error("카지노 환경설정 업데이트에 실패하였습니다.", e);
            return false;
        }
        return true;
    }

    public Page<CasinoEvolutionBet> betting(CasinoEvolutionDto.Command command, Pageable pageable) {
        QCasinoEvolutionBet bet = QCasinoEvolutionBet.casinoEvolutionBet;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.notEmpty(command.getUserid())) {
            builder.and(bet.userid.contains(command.getUserid()).or(bet.nickname.contains(command.getUserid())));
        }

        if (StringUtils.notEmpty(command.getCasinoId())) {
            builder.and(bet.casinoId.contains(command.getCasinoId()));
        }

        return casinoEvolutionBetRepository.findAll(builder, pageable);
    }
}
